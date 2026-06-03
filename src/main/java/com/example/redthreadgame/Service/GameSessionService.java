package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.GameSessionIn;
import com.example.redthreadgame.DTO.OUT.GameSessionOut;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Repository.CaseRepository;
import com.example.redthreadgame.Repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    private final ModelMapper modelMapper;
    private final GameSessionRepository gameSessionRepository;
    private final CaseRepository caseRepository;


    //BASIC CRUD
    public List<GameSessionOut> getAllGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAll()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void addGameSession(Integer caseId,GameSessionIn gameSessionIn){
        Case sessionCase = checkCase(caseId);

        GameSession gameSession = modelMapper.map(gameSessionIn, GameSession.class);
        gameSession.setSessionCase(sessionCase);
        gameSession.setStatus("PENDING");

        gameSessionRepository.save(gameSession);
    }

    public void updateGameSession(Integer id, GameSessionIn gameSessionIn){
        GameSession oldGameSession = checkGameSession(id);

        oldGameSession.setPlayersCount(gameSessionIn.getPlayersCount());
        oldGameSession.setIsPrivate(gameSessionIn.getIsPrivate());

        gameSessionRepository.save(oldGameSession);
    }

    public void deleteGameSession(Integer id){
        GameSession gameSession = checkGameSession(id);
        gameSessionRepository.delete(gameSession);
    }


    //EXTRA ENDPOINTS
    public void updateStatus(Integer id){
        GameSession gameSession = checkGameSession(id);
        if(gameSession.getStatus().equalsIgnoreCase("PENDING"))
            gameSession.setStatus("IN_PROGRESS");
        else if(gameSession.getStatus().equalsIgnoreCase("IN_PROGRESS"))
            gameSession.setStatus("COMPLETED");
        else throw new ApiException("game session is completed you cannot change the status");
    }


    //HELPER METHODS
    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }

    private Case checkCase(Integer id){
        Case sessionCase = caseRepository.findCaseById(id);
        if(sessionCase == null) throw new ApiException("Case not found"); //check case

        return sessionCase;
    }
}
