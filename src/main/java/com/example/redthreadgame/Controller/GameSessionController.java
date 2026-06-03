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

    @PostMapping("/add")
    public ResponseEntity<?> addGameSession(@RequestBody @Valid GameSessionIn gameSession){
        gameSessionService.addGameSession(gameSession);
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
}
