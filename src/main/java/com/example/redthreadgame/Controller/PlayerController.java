package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.PlayerIn;
import com.example.redthreadgame.Service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    //BASIC CRUD ENDPOINTS
    @GetMapping("/get")
    public ResponseEntity<?> getAllPlayers(){
        return ResponseEntity.status(200).body(playerService.getAllPlayers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPlayer(@RequestBody @Valid PlayerIn player){
        playerService.addPlayer(player);
        return ResponseEntity.status(200).body(new ApiResponse("Player added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Integer id, @RequestBody @Valid PlayerIn player){
        playerService.updatePlayer(id, player);
        return ResponseEntity.status(200).body(new ApiResponse("Player updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer id){
        playerService.deletePlayer(id);
        return ResponseEntity.status(200).body(new ApiResponse("Player deleted successfully"));
    }
}
