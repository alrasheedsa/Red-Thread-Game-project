package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.Service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitation")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    //BASIC CRUD ENDPOINTS
    @GetMapping("/get")
    public ResponseEntity<?> getAllInvitations(){
        return ResponseEntity.status(200).body(invitationService.getAllInvitations());
    }

    @PostMapping("/add/{ownerId}/{gameSessionId}/{playerId}")
    public ResponseEntity<?> addInvitation(@PathVariable Integer ownerId, @PathVariable Integer gameSessionId, @PathVariable Integer playerId){
        invitationService.addInvitation(ownerId, gameSessionId, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation added successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInvitation(@PathVariable Integer id){
        invitationService.deleteInvitation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation deleted successfully"));
    }


    //EXTRA ENDPOINTS
    @PutMapping("/reject-invitation/{gameSessionId}/{playerId}")
    public ResponseEntity<?> rejectInvitation(@PathVariable Integer gameSessionId, @PathVariable Integer playerId){
        invitationService.rejectInvitation(gameSessionId, playerId);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation rejected successfully"));
    }
}
