package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.Model.Hint;
import com.example.redthreadgame.Repository.HintRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HintService {

    private final HintRepository hintRepository;
//    private final GameSessionRepository gameSessionRepository;
    private final ModelMapper modelMapper;

//    public List<HintOut> getHintsByGameSession(Integer gameSessionId) {
//        List<HintOut> hints = new ArrayList<>();
//
//        for (Hint h : hintRepository.findAllByGameSessionId(gameSessionId)) {
//            hints.add(modelMapper.map(h, HintOut.class));
//        }
//
//        return hints;
//    }
//
//    public void addHint(Integer gameSessionId, HintIn dto) {
//        GameSessionModel gameSession = gameSessionRepository.findById(gameSessionId)
//                .orElseThrow(() -> new ApiException("Game session not found"));
//
//        Hint hint = modelMapper.map(dto, Hint.class);
//        hint.setGameSession(gameSession);
//
//        hintRepository.save(hint);
//    }

    public void deleteHint(Integer hintId) {
        Hint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new ApiException("Hint not found"));

        hintRepository.delete(hint);
    }
}
