package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.GameSessionIn;
import com.example.redthreadgame.Service.GameSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game-session")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;

    //BASIC CRUD ENDPOINTS
    @GetMapping("/get")
    public ResponseEntity<?> getAllGameSessions(){
        return ResponseEntity.status(200).body(gameSessionService.getAllGameSessions());
    }

    @PostMapping("/add/{caseId}/{playerId}")
    public ResponseEntity<?> addGameSession(@PathVariable Integer caseId,@PathVariable Integer playerId, @RequestBody @Valid GameSessionIn gameSession){
        gameSessionService.addGameSession(caseId ,playerId,gameSession);
        return ResponseEntity.status(200).body(new ApiResponse("Game Session added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGameSession(@PathVariable Integer id, @RequestBody @Valid GameSessionIn gameSession){
        gameSessionService.updateGameSession(id, gameSession);
        return ResponseEntity.status(200).body(new ApiResponse("Game Session updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGameSession(@PathVariable Integer id){
        gameSessionService.deleteGameSession(id);
        return ResponseEntity.status(200).body(new ApiResponse("Game Session deleted successfully"));
    }


    //ِِEXTRA ENDPOINTS
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id){
        gameSessionService.updateStatus(id);
        return ResponseEntity.status(200).body(new ApiResponse("Game Session status updated successfully"));
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicGameSessions(){
        return ResponseEntity.status(200).body(gameSessionService.getPublicGameSessions());
    }

    @PostMapping("/join/public/{gameSessionId}/{playerId}")
    public ResponseEntity<?> joinPublicGameSession(@PathVariable Integer gameSessionId, @PathVariable Integer playerId){
        gameSessionService.joinPublicGameSession(gameSessionId, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Joined game session successfully"));
    }

    @PostMapping("/join/private/{sessionCode}/{playerId}")
    public ResponseEntity<?> joinPrivateGameSession(@PathVariable String sessionCode, @PathVariable Integer playerId){
        gameSessionService.joinPrivateGameSession(sessionCode, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Joined game session successfully"));
    }

    @DeleteMapping("/leave/{gameSessionId}/{playerId}")
    public ResponseEntity<?> leaveSession(@PathVariable Integer gameSessionId, @PathVariable Integer playerId){
        gameSessionService.leaveSession(gameSessionId, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Left session successfully"));
    }

    @PutMapping("/start/{gameSessionId}/{playerId}")
    public ResponseEntity<?> startSession(@PathVariable Integer gameSessionId, @PathVariable Integer playerId){
        gameSessionService.startSession(gameSessionId, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Game session started successfully"));
    }
}
