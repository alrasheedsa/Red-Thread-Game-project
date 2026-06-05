package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.OUT.QuestionOut;
import com.example.redthreadgame.DTO.OUT.VoiceAnswerOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.QuestionTargetType;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final WitnessRepository witnessRepository;
    private final SuspectRepository suspectRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final OpenAiService openAiService;
    private final ElevenLabsService elevenLabsService;
    private final ModelMapper modelMapper;

    public List<QuestionOut> getAllQuestions() {
        List<QuestionOut> questions = new ArrayList<>();

        for (Question q : questionRepository.findAll()) {
            questions.add(modelMapper.map(q, QuestionOut.class));
        }

        return questions;
    }

    public List<QuestionOut> getQuestionsByGameSession(Integer gameSessionId) {
        List<QuestionOut> questions = new ArrayList<>();

        for (Question q : questionRepository.findAllByGameSessionId(gameSessionId)) {
            questions.add(modelMapper.map(q, QuestionOut.class));
        }

        return questions;
    }

    public List<QuestionOut> getQuestionsByPlayer(Integer playerId) {
        List<QuestionOut> questions = new ArrayList<>();

        for (Question q : questionRepository.findAllByPlayerId(playerId)) {
            questions.add(modelMapper.map(q, QuestionOut.class));
        }

        return questions;
    }

    public VoiceAnswerOut askWitnessQuestion(Integer gameSessionId, Integer playerId, Integer witnessId, QuestionIn dto) {
        GameSession gameSession = checkGameSession(gameSessionId);
        Player player = checkPlayer(playerId);
        checkCanPlay(gameSession, player);

        Witness witness = checkWitness(witnessId);
        if (!witness.getWitnessCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Witness does not belong to this game session case");

        Case sessionCase = gameSession.getSessionCase();
        String prompt = """
                You are roleplaying as a witness in a detective mystery game.

                Rules:
                - Answer only as the current witness.
                - Use the case scenario, witnesses, suspects, and evidences below.
                - Stay consistent with the current witness statement and reliability score.
                - Do not reveal the full solution.
                - Do not invent evidence, suspects, witnesses, dates, places, or actions not listed below.
                - If the question is outside what this witness could know, say you do not know.
                - If the player asks directly who is guilty, answer from this witness perspective only.
                - Keep the answer natural, short, and useful for investigation.
                - Answer in the same language as the player's question.

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

                Current witness:
                Name: %s
                Statement: %s
                Reliability score: %s

                Player question:
                %s
                """.formatted(
                sessionCase.getTitle(),
                sessionCase.getScenario(),
                buildWitnessesText(sessionCase),
                buildSuspectsText(sessionCase),
                buildEvidencesText(sessionCase),
                witness.getName(),
                witness.getStatement(),
                witness.getReliabilityScore(),
                dto.getQuestionText()
        );

        String answer = openAiService.generateAnswer(prompt);
        String audioFileName = elevenLabsService.generateVoice(answer);

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.WITNESS);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setWitness(witness);
        question.setAnswerText(answer);
        question.setVoiceUrl(audioFileName);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
        return new VoiceAnswerOut(answer, audioFileName);
    }

    public VoiceAnswerOut askSuspectQuestion(Integer gameSessionId, Integer playerId, Integer suspectId, QuestionIn dto) {
        GameSession gameSession = checkGameSession(gameSessionId);
        Player player = checkPlayer(playerId);
        checkCanPlay(gameSession, player);

        Suspect suspect = checkSuspect(suspectId);
        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this game session case");

        Case sessionCase = gameSession.getSessionCase();
        String prompt = """
                You are roleplaying as a suspect in a detective mystery game.

                Rules:
                - Answer only as the current suspect.
                - Use the case scenario, witnesses, suspects, and evidences below.
                - Defend yourself naturally, but stay consistent with the case facts.
                - Do not reveal the full solution.
                - Do not confess unless the provided case facts clearly force it.
                - Do not invent evidence, suspects, witnesses, dates, places, or actions not listed below.
                - If the question is outside what this suspect could know, say you do not know.
                - Keep the answer natural, short, and useful for investigation.
                - Answer in the same language as the player's question.

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

                Current suspect:
                Name: %s
                Age: %s

                Player question:
                %s
                """.formatted(
                sessionCase.getTitle(),
                sessionCase.getScenario(),
                buildWitnessesText(sessionCase),
                buildSuspectsText(sessionCase),
                buildEvidencesText(sessionCase),
                suspect.getName(),
                suspect.getAge(),
                dto.getQuestionText()
        );

        String answer = openAiService.generateAnswer(prompt);
        String audioFileName = elevenLabsService.generateVoice(answer);

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.SUSPECT);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setSuspect(suspect);
        question.setAnswerText(answer);
        question.setVoiceUrl(audioFileName);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
        return new VoiceAnswerOut(answer, audioFileName);
    }

    public void updateQuestion(Integer questionId, QuestionIn dto) {
        Question question = checkQuestion(questionId);

        question.setQuestionText(dto.getQuestionText());
        question.setAnswerText(dto.getAnswerText());
        question.setVoiceUrl(dto.getVoiceUrl());

        questionRepository.save(question);
    }

    public void deleteQuestion(Integer questionId) {
        Question question = checkQuestion(questionId);
        questionRepository.delete(question);
    }

    public List<QuestionOut> getQuestionsByWitnessId(Integer witnessId) {
        List<QuestionOut> questions = new ArrayList<>();

        for(Question q : questionRepository.findAllByWitnessId(witnessId)) {
            questions.add(modelMapper.map(q, QuestionOut.class));
        }
        return  questions;
    }

    public List<QuestionOut> getQuestionsBySuspectId(Integer suspectId) {
        List<QuestionOut> questions = new ArrayList<>();
        for(Question q : questionRepository.findAllBySuspectId(suspectId)) {
            questions.add(modelMapper.map(q, QuestionOut.class));
        }
        return questions;
    }


    private Question checkQuestion(Integer id) {
        Question question = questionRepository.findQuestionById(id);
        if (question == null) throw new ApiException("Question not found");
        return question;
    }

    private GameSession checkGameSession(Integer id) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if (gameSession == null) throw new ApiException("Game session not found");
        return gameSession;
    }

    private Player checkPlayer(Integer id) {
        Player player = playerRepository.findPlayerById(id);
        if (player == null) throw new ApiException("Player not found");
        return player;
    }

    private Witness checkWitness(Integer id) {
        Witness witness = witnessRepository.findWitnessById(id);
        if (witness == null) throw new ApiException("Witness not found");
        return witness;
    }

    private Suspect checkSuspect(Integer id) {
        Suspect suspect = suspectRepository.findSuspectById(id);
        if (suspect == null) throw new ApiException("Suspect not found");
        return suspect;
    }

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }



    //لترتيب output ask witness& Suspect
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
