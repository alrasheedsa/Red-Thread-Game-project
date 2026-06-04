package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.OUT.QuestionOut;
import com.example.redthreadgame.Enums.QuestionTargetType;
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

    public void addWitnessQuestion(Integer gameSessionId, Integer playerId, Integer witnessId, QuestionIn dto) {
        GameSession gameSession = checkGameSession(gameSessionId);
        Player player = checkPlayer(playerId);
        Witness witness = checkWitness(witnessId);

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.WITNESS);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setWitness(witness);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
    }

    public void addSuspectQuestion(Integer gameSessionId, Integer playerId, Integer suspectId, QuestionIn dto) {
        GameSession gameSession = checkGameSession(gameSessionId);
        Player player = checkPlayer(playerId);
        Suspect suspect = checkSuspect(suspectId);

        Question question = modelMapper.map(dto, Question.class);
        question.setTargetType(QuestionTargetType.SUSPECT);
        question.setGameSession(gameSession);
        question.setPlayer(player);
        question.setSuspect(suspect);
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
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
}
