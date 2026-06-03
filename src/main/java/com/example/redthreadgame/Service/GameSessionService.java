package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.GameSessionIn;
import com.example.redthreadgame.DTO.OUT.GameSessionOut;
import com.example.redthreadgame.Model.GameSession;
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


    //BASIC CRUD
    public List<GameSessionOut> getAllGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAll()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void addGameSession(GameSessionIn gameSessionIn){
        GameSession gameSession = modelMapper.map(gameSessionIn, GameSession.class);
        gameSession.setStatus("PENDING");
        gameSession.setScore(100);
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



    //HELPER METHODS
    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }
}
