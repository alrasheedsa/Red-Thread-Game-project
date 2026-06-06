package com.example.redthreadgame.Service;
import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.SolutionProposalIn;
import com.example.redthreadgame.DTO.OUT.SolutionProposalOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Enums.SolutionProposalStatusType;
import com.example.redthreadgame.Model.CaseSolution;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Model.SolutionProposal;
import com.example.redthreadgame.Model.Suspect;
import com.example.redthreadgame.Repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionProposalService {

    private final SolutionProposalRepository solutionProposalRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final SuspectRepository suspectRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final CaseSolutionRepository caseSolutionRepository;
    private final HintService hintService;
    private final OpenAiService openAiService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public List<SolutionProposalOut> getProposalsByGameSession(Integer gameSessionId) {
        List<SolutionProposalOut> proposals = new ArrayList<>();

        for (SolutionProposal s : solutionProposalRepository.findAllByGameSessionId(gameSessionId)) {
            proposals.add(modelMapper.map(s, SolutionProposalOut.class));
        }

        return proposals;
    }

    public void submitProposal(Integer gameSessionId, Integer playerId, Integer suspectId, SolutionProposalIn dto) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        Suspect suspect = suspectRepository.findById(suspectId)
                .orElseThrow(() -> new ApiException("Suspect not found"));

        checkCanPlay(gameSession, player);
        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this game session case");

        SolutionProposal proposal = modelMapper.map(dto, SolutionProposal.class);
        proposal.setStatus(SolutionProposalStatusType.PENDING);
        proposal.setAcceptCount(0);
        proposal.setRejectCount(0);
        proposal.setGameSession(gameSession);
        proposal.setPlayer(player);
        proposal.setSuspect(suspect);

        solutionProposalRepository.save(proposal);
    }

    public void acceptProposal(Integer proposalId) {
        throw new ApiException("Use proposal vote endpoint instead");
    }

    public void rejectProposal(Integer proposalId) {
        throw new ApiException("Use proposal vote endpoint instead");
    }

    public void changeStatus(Integer proposalId, String status) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setStatus(SolutionProposalStatusType.valueOf(status));
        solutionProposalRepository.save(proposal);
    }

    public List<SolutionProposalOut> getProposalsByPlayer(Integer playerId) {
        List<SolutionProposalOut> proposals = new ArrayList<>();

        for (SolutionProposal s : solutionProposalRepository.findAllByPlayerId(playerId)) {
            proposals.add(modelMapper.map(s, SolutionProposalOut.class));
        }

        return proposals;
    }

    public void markProposalCorrect(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setStatus(SolutionProposalStatusType.CORRECT);
        solutionProposalRepository.save(proposal);
    }

    public void markProposalWrong(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setStatus(SolutionProposalStatusType.WRONG);
        solutionProposalRepository.save(proposal);
    }
    public Integer evaluateProposal(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        GameSession gameSession = proposal.getGameSession();
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        Integer requiredVotes = (int) Math.ceil(gameSession.getPlayersCount() / 2.0);
        if (proposal.getAcceptCount() < requiredVotes)
            throw new ApiException("Proposal does not have majority accepted votes");

        CaseSolution solution = caseSolutionRepository.findCaseSolutionById(gameSession.getSessionCase().getId());
        if (solution == null)
            throw new ApiException("Case solution not found");

        List<SessionPlayer> joinedPlayers = new ArrayList<>();
        for (SessionPlayer s : sessionPlayerRepository.findAllByGameSessionId(gameSession.getId())) {
            if (s.getStatus() == SessionPlayerStatus.JOINED) {
                joinedPlayers.add(s);
            }
        }

        if (joinedPlayers.isEmpty())
            throw new ApiException("No joined players found in this game session");

        String analysisResult = openAiService.evaluateSolution(proposal.getReason(), proposal.getSuspect().getName(), proposal.getSuspect().getAge(), solution.getJustification());

        boolean isCorrect;
        try {
            JsonNode analysisJson = new ObjectMapper().readTree(analysisResult);
            isCorrect = analysisJson.path("isCorrect").asBoolean();
        } catch (Exception e) {
            throw new ApiException("Failed to parse AI response");
        }

        if (!isCorrect) {
            proposal.setStatus(SolutionProposalStatusType.WRONG);
            gameSession.setScore(0);
            gameSession.setStatus(GameSessionStatusType.LOST);
            gameSession.setEndedAt(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            solutionProposalRepository.save(proposal);
            notifyPlayersWrongSolution(gameSession, proposal, joinedPlayers);
            return 0;
        }

        Integer totalScore = hintService.calculateTotalScore(gameSession.getId());
        Integer playerScore = totalScore / joinedPlayers.size();

        proposal.setStatus(SolutionProposalStatusType.CORRECT);
        gameSession.setScore(totalScore);
        gameSession.setStatus(GameSessionStatusType.WON);
        gameSession.setEndedAt(LocalDateTime.now());

        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();
            player.setScore(player.getScore() + playerScore);
            playerRepository.save(player);
        }

        gameSessionRepository.save(gameSession);
        solutionProposalRepository.save(proposal);
        notifyPlayersCorrectSolution(gameSession, proposal, totalScore, playerScore, joinedPlayers);
        return totalScore;
    }

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }

    private void notifyPlayersCorrectSolution(GameSession gameSession, SolutionProposal proposal, Integer totalScore, Integer playerScore, List<SessionPlayer> joinedPlayers) {
        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();
            emailService.send(
                    player.getEmail(),
                    "Red Thread Game Result - Case Solved",
                    "Dear " + player.getName() + ",\n\n" +
                            "Your team solved the case successfully.\n\n" +
                            "Case: " + gameSession.getSessionCase().getTitle() + "\n" +
                            "Accused suspect: " + proposal.getSuspect().getName() + "\n" +
                            "Final session score: " + totalScore + "\n" +
                            "Your earned score: " + playerScore + "\n" +
                            "Ended at: " + gameSession.getEndedAt() + "\n\n" +
                            "Great detective work.\n\n" +
                            "Red Thread Game Team"
            );
        }
    }

    private void notifyPlayersWrongSolution(GameSession gameSession, SolutionProposal proposal, List<SessionPlayer> joinedPlayers) {
        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();
            emailService.send(
                    player.getEmail(),
                    "Red Thread Game Result - Case Failed",
                    "Dear " + player.getName() + ",\n\n" +
                            "Your team submitted a wrong solution.\n\n" +
                            "Case: " + gameSession.getSessionCase().getTitle() + "\n" +
                            "Accused suspect: " + proposal.getSuspect().getName() + "\n" +
                            "Final session score: 0\n" +
                            "Your earned score: 0\n" +
                            "Ended at: " + gameSession.getEndedAt() + "\n\n" +
                            "Better luck next investigation.\n\n" +
                            "Red Thread Game Team"
            );
        }
    }
}
