package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.Service.SessionPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/session-player")
@RestController
@RequiredArgsConstructor
public class SessionPlayerController {

    private final SessionPlayerService sessionPlayerService;

    //BASIC CRUD ENDPOINTS
    @GetMapping("/get")
    public ResponseEntity<?> getAllSessionPlayers() {
        return ResponseEntity.status(200).body(sessionPlayerService.getAllSessionPlayers());
    }

    @DeleteMapping("/delete/{sessionPlayerId}")
    public ResponseEntity<?> deleteSessionPlayer(@PathVariable Integer sessionPlayerId) {
        sessionPlayerService.deleteSessionPlayer(sessionPlayerId);
        return ResponseEntity.status(200).body(new ApiResponse("Session player deleted successfully"));
    }


    //EXTRA ENDPOINTS
    @GetMapping("/session-members/{gameSessionId}")
    public ResponseEntity<?> getSessionMembers(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(sessionPlayerService.getSessionMembers(gameSessionId));
    }

    @GetMapping("/player-history/{playerId}")
    public ResponseEntity<?> getPlayerSessionHistory(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(sessionPlayerService.getPlayerSessionHistory(playerId));
    }
}
