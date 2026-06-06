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

    @PostMapping("/add/{gameSessionId}")
    public ResponseEntity<?> addHint(@PathVariable Integer gameSessionId, @RequestBody @Valid HintIn dto) {
        hintService.addHint(gameSessionId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Hint added successfully"));
    }

    @PostMapping("/request/{gameSessionId}/{playerId}")
    public ResponseEntity<?> requestHint(@PathVariable Integer gameSessionId, @PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(hintService.requestHint(gameSessionId, playerId));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getHintsByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(hintService.getHintsByPlayer(playerId));
    }


    @GetMapping("/penalty/{gameSessionId}")
    public ResponseEntity<?> calculateHintPenalty(@PathVariable Integer gameSessionId) {
        Integer penalty = hintService.calculateHintPenalty(gameSessionId);
        return ResponseEntity.status(200).body(penalty);
    }

    @GetMapping("/total-score/{gameSessionId}")
    public ResponseEntity<?> calculateTotalScore(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(hintService.calculateTotalScore(gameSessionId));
    }

    @DeleteMapping("/delete/{hintId}")
    public ResponseEntity<?> deleteHint(@PathVariable Integer hintId) {
        hintService.deleteHint(hintId);
        return ResponseEntity.status(200).body(new ApiResponse("Hint deleted successfully"));
    }
}
