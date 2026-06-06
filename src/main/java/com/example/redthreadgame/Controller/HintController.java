package com.example.redthreadgame.Controller;
import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.HintIn;
import com.example.redthreadgame.Service.HintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/hint")
@RestController
@RequiredArgsConstructor
public class HintController {

    private final HintService hintService;

    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getHintsByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(hintService.getHintsByGameSession(gameSessionId));
    }
    @GetMapping("/count/{gameSessionId}")
    public ResponseEntity<?> getHintsCountBySession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(hintService.getHintsCountBySession(gameSessionId));
    }
    @GetMapping("/next-penalty/{gameSessionId}")
    public ResponseEntity<?> getNextHintPenalty(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(hintService.getNextHintPenalty(gameSessionId));
    }
    @PostMapping("/request/{gameSessionId}/{playerId}")
    public ResponseEntity<?> requestHint(@PathVariable Integer gameSessionId, @PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(hintService.requestHint(gameSessionId, playerId));
    }
}
