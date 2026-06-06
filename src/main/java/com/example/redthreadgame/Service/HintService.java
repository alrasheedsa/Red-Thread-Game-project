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
    public HintOut requestHint(Integer gameSessionId, Integer playerId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        checkCanPlay(gameSession, player);//check if player in joined and game session in progress

        Case sessionCase = gameSession.getSessionCase();
        String prompt = """
                You are generating a hint for a detective mystery game.

                Rules:
                - Give exactly one useful hint.
                - Do not reveal the culprit directly.
                - Do not reveal the full solution.
                - Do not invent new facts, evidence, witnesses, suspects, places, or events.
                - Guide the player toward an important clue, contradiction, witness detail, suspect behavior, or evidence.
                - Make the hint specific to this case.
                - Keep it short and natural.
                - Answer in English.

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
    }// Generates AI hint based on the current case, saves it, and deduct session score after the free hint is used

    public Integer calculateTotalScore(Integer gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        return gameSession.getScore();
    }
    public Integer getHintsCountBySession(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        return hintRepository.findAllByGameSessionId(gameSessionId).size();
    }
    public Integer getNextHintPenalty(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Integer hintsCount = hintRepository.findAllByGameSessionId(gameSessionId).size();

        if (hintsCount == 0)
            return 0;

        return 3;
    }// Show the penalty that will be applied if the team request another hint now

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }// Allow hint request only while the session is in progress and the player is joined

    private void deductHintScoreIfNeeded(GameSession gameSession, Integer deductedPoints) {
        if (deductedPoints > 0) {
            gameSession.setScore(Math.max(0, gameSession.getScore() - deductedPoints));
            gameSessionRepository.save(gameSession);
        }
    }// Deduct hint penalty from the shared session score without allowing it to go below zero

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