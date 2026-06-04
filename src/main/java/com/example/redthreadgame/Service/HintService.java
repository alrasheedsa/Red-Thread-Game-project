package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.HintIn;
import com.example.redthreadgame.DTO.OUT.HintOut;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Hint;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.HintRepository;
import com.example.redthreadgame.Repository.PlayerRepository; // تم إضافة الاستيراد
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
        hint.setGameSession(gameSession);

        hintRepository.save(hint);
    }

    public void requestHint(Integer gameSessionId, Integer playerId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        Hint hint = new Hint();
        hint.setContent("Focus on the strongest evidence and compare it with the suspect timeline");
        hint.setDeductedPoints(5);
        hint.setGameSession(gameSession);
        hint.setPlayer(player);

        hintRepository.save(hint);
    }

    public List<HintOut> getHintsByPlayer(Integer playerId) {
        Player player = playerRepository.findById(playerId)
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

        List<Hint> hints = hintRepository.findAllByGameSessionId(gameSessionId);

        // افتراض: كل تلميح مستخدم يخصم 5 نقاط من اللاعب
        int penaltyPerHint = 5;
        return hints.size() * penaltyPerHint;
    }

    public void deleteHint(Integer hintId) {
        Hint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new ApiException("Hint not found"));

        hintRepository.delete(hint);
    }
}
