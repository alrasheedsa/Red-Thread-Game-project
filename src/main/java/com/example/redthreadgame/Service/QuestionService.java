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
                - Answer in English only.
                - Write with a natural tone matching the witness voiceTone.
                - Do not include stage directions, brackets, emotion labels, or sound effects.
                - Show the tone through natural wording, hesitation, confidence, or caution.

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
                Gender: %s
                Voice tone: %s

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
                witness.getGender(),
                witness.getVoiceTone(),
                dto.getQuestionText()
        );

        String answer = openAiService.generateAnswer(prompt);
        String audioFileName = elevenLabsService.generateVoice(answer, witness.getGender(), witness.getVoiceTone());

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.WITNESS);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setWitness(witness);
        question.setAnswerText(answer);
        question.setVoiceUrl(audioFileName);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
        deductQuestionScoreIfNeeded(gameSession);
        return new VoiceAnswerOut(answer, audioFileName);
    }// Build the witness prompt, generates AI answer and voice, saves the question, then updates session question penalty

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
                - Answer in English only.
                - Write with a natural tone matching the suspect voiceTone.
                - Do not include stage directions, brackets, emotion labels, or sound effects.
                - Show the tone through natural wording, hesitation, confidence, defensiveness, or caution.

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
                Gender: %s
                Voice tone: %s

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
                suspect.getGender(),
                suspect.getVoiceTone(),
                dto.getQuestionText()
        );

        String answer = openAiService.generateAnswer(prompt);
        String audioFileName = elevenLabsService.generateVoice(answer, suspect.getGender(), suspect.getVoiceTone());

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.SUSPECT);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setSuspect(suspect);
        question.setAnswerText(answer);
        question.setVoiceUrl(audioFileName);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
        deductQuestionScoreIfNeeded(gameSession);
        return new VoiceAnswerOut(answer, audioFileName);
    }// Build the suspect prompt, generates AI answer and voice, saves the question, then updates session question penalty

    public Integer getQuestionsCountBySession(Integer gameSessionId) {
        checkGameSession(gameSessionId);
        return questionRepository.findAllByGameSessionId(gameSessionId).size();
    }
    public Integer getNextQuestionPenalty(Integer gameSessionId) {
        checkGameSession(gameSessionId);

        Integer questionsCount = questionRepository.findAllByGameSessionId(gameSessionId).size();

        if (questionsCount < 2)
            return 0;

        return 2;
    }// Show whether the next team question is free or will deduct points


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
    private void deductQuestionScoreIfNeeded(GameSession gameSession) {
        Integer questionsCount = questionRepository.findAllByGameSessionId(gameSession.getId()).size();
        gameSession.setQuestionsCount(questionsCount);

        if (questionsCount > 2) {
            gameSession.setScore(Math.max(0, gameSession.getScore() - 2));
        }

        gameSessionRepository.save(gameSession);
    }// Keeps the first two team questions free, then deducts points from the shared session score.

    private String buildWitnessesText(Case sessionCase) {
        String text = "";
        for (Witness w : sessionCase.getWitnesses()) {
            text += "- " + w.getName() + ": " + w.getStatement() + " Reliability: " + w.getReliabilityScore() + " Gender: " + w.getGender() + " Voice tone: " + w.getVoiceTone() + "\n";
        }
        return text;
    }

    private String buildSuspectsText(Case sessionCase) {
        String text = "";
        for (Suspect s : sessionCase.getSuspects()) {
            text += "- " + s.getName() + ", age " + s.getAge() + " Gender: " + s.getGender() + " Voice tone: " + s.getVoiceTone() + "\n";
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
