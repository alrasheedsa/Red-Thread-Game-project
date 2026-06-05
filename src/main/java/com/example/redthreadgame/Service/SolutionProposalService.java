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

        if (proposal.getAcceptCount() <= gameSession.getPlayersCount() / 2)
            throw new ApiException("Proposal does not have majority accepted votes");

        CaseSolution solution = caseSolutionRepository.findCaseSolutionById(gameSession.getSessionCase().getId());
        if (solution == null)
            throw new ApiException("Case solution not found");

        Boolean isCorrect = openAiService.evaluateSolution(proposal.getReason(), solution.getJustification());
        if (!isCorrect) {
            proposal.setStatus(SolutionProposalStatusType.WRONG);
            gameSession.setScore(0);
            gameSession.setStatus(GameSessionStatusType.COMPLETED);
            gameSession.setEndedAt(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            solutionProposalRepository.save(proposal);
            return 0;
        }

        Integer totalScore = hintService.calculateTotalScore(gameSession.getId());
        proposal.setStatus(SolutionProposalStatusType.CORRECT);
        gameSession.setScore(totalScore);
        gameSession.setStatus(GameSessionStatusType.COMPLETED);
        gameSession.setEndedAt(LocalDateTime.now());

        for (SessionPlayer s : sessionPlayerRepository.findAllByGameSessionId(gameSession.getId())) {
            if (s.getStatus() == SessionPlayerStatus.JOINED) {
                Player player = s.getPlayer();
                if (player.getScore() == null)
                    player.setScore(0);
                player.setScore(player.getScore() + totalScore);
                playerRepository.save(player);
            }
        }

        gameSessionRepository.save(gameSession);
        solutionProposalRepository.save(proposal);
        return totalScore;
    }

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }
}
