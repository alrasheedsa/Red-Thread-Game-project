package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.OUT.SessionPlayerOut;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionPlayerService {
    private final SessionPlayerRepository sessionPlayerRepository;
    private final ModelMapper modelMapper;


    //BASIC CRUD
    public List<SessionPlayerOut> getAllSessionPlayers() {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();
        for (SessionPlayer s : sessionPlayerRepository.findAll()) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }
        return sessionPlayers;
    }

    public void deleteSessionPlayer(Integer id) {
        SessionPlayer sessionPlayer = checkSessionPlayer(id);
        sessionPlayerRepository.delete(sessionPlayer);
    }


    //EXTRA ENDPOINTS
    public List<SessionPlayerOut> getSessionMembers(Integer gameSessionId) {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();
        for (SessionPlayer s : sessionPlayerRepository.findAllByGameSessionId(gameSessionId)) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }
        return sessionPlayers;
    }

    public List<SessionPlayerOut> getPlayerSessionHistory(Integer playerId) {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();
        for (SessionPlayer s : sessionPlayerRepository.findAllByPlayerId(playerId)) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }
        return sessionPlayers;
    }


    //HELPER METHODS
    private SessionPlayer checkSessionPlayer(Integer id) {
        SessionPlayer sessionPlayer = sessionPlayerRepository.findSessionPlayerById(id);
        if (sessionPlayer == null) throw new ApiException("Session player not found");
        return sessionPlayer;
    }

}
