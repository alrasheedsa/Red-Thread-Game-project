package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.HintIn;
import com.example.redthreadgame.DTO.OUT.HintOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.HintRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HintService {

    private final HintRepository hintRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final OpenAiService openAiService;
    private final ModelMapper modelMapper;

    public List<HintOut> getHintsByGameSession(Integer gameSessionId) {
        List<HintOut> hints = new ArrayList<>();

        for (Hint h : hintRepository.findAllByGameSessionId(gameSessionId)) {
            hints.add(modelMapper.map(h, HintOut.class));
        }

        return hints;
    }

    public void addHint(Integer gameSessionId, HintIn dto) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Hint hint = modelMapper.map(dto, Hint.class);
        hint.setDeductedPoints(5);
        hint.setGameSession(gameSession);

        hintRepository.save(hint);
    }

    public HintOut requestHint(Integer gameSessionId, Integer playerId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        checkCanPlay(gameSession, player);

        Case sessionCase = gameSession.getSessionCase();
        String prompt = """
                You are generating a hint for a detective mystery game in English only.

                Rules:
                - Give exactly one useful hint.
                - Do not reveal the culprit directly.
                - Do not reveal the full solution.
                - Do not invent new facts, evidence, witnesses, suspects, places, or events.
                - Guide the player toward an important clue, contradiction, witness detail, suspect behavior, or evidence.
                - Make the hint specific to this case.
                - Keep it short and natural.
                - Answer in Arabic if possible.

                Case title:
                %s

                Case scenario:
                %s

                Witnesses:
                %s

                Suspects:
                %s

                Evidences:
                %s

                Hint:
                """.formatted(
                sessionCase.getTitle(),
                sessionCase.getScenario(),
                buildWitnessesText(sessionCase),
                buildSuspectsText(sessionCase),
                buildEvidencesText(sessionCase)
        );

        String hintContent = openAiService.generateAnswer(prompt);
        Integer hintsCount = hintRepository.findAllByGameSessionId(gameSession.getId()).size() + 1;
        Integer deductedPoints = hintsCount > 1 ? 3 : 0;

        Hint hint = new Hint();
        hint.setContent(hintContent);
        hint.setDeductedPoints(deductedPoints);
        hint.setGameSession(gameSession);
        hint.setPlayer(player);

        hintRepository.save(hint);
        deductHintScoreIfNeeded(gameSession, deductedPoints);
        return modelMapper.map(hint, HintOut.class);
    }

    public List<HintOut> getHintsByPlayer(Integer playerId) {
        playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        List<HintOut> hints = new ArrayList<>();

        for (Hint h : hintRepository.findAllByPlayerId(playerId)) {
            hints.add(modelMapper.map(h, HintOut.class));
        }

        return hints;
    }

    public Integer calculateHintPenalty(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Integer totalPenalty = 0;
        for (Hint h : hintRepository.findAllByGameSessionId(gameSessionId)) {
            if (h.getDeductedPoints() != null)
                totalPenalty += h.getDeductedPoints();
        }

        return totalPenalty;
    }

    public Integer calculateTotalScore(Integer gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        return gameSession.getScore();
    }

    public void deleteHint(Integer hintId) {
        Hint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new ApiException("Hint not found"));

        hintRepository.delete(hint);
    }

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }

    private void deductHintScoreIfNeeded(GameSession gameSession, Integer deductedPoints) {
        if (deductedPoints > 0) {
            gameSession.setScore(Math.max(0, gameSession.getScore() - deductedPoints));
            gameSessionRepository.save(gameSession);
        }
    }

    private String buildWitnessesText(Case sessionCase) {
        String text = "";
        for (Witness w : sessionCase.getWitnesses()) {
            text += "- " + w.getName() + ": " + w.getStatement() + " Reliability: " + w.getReliabilityScore() + "\n";
        }
        return text;
    }

    private String buildSuspectsText(Case sessionCase) {
        String text = "";
        for (Suspect s : sessionCase.getSuspects()) {
            text += "- " + s.getName() + ", age " + s.getAge() + "\n";
        }
        return text;
    }

    private String buildEvidencesText(Case sessionCase) {
        String text = "";
        for (Evidence e : sessionCase.getEvidences()) {
            text += "- " + e.getTitle() + ": " + e.getDescription() + "\n";
        }
        return text;
    }
}
