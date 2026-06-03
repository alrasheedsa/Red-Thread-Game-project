package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.InvitationIn;
import com.example.redthreadgame.Service.InvitationService;
import jakarta.validation.Valid;
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

    @PostMapping("/add")
    public ResponseEntity<?> addInvitation(@RequestBody @Valid InvitationIn invitation){
        invitationService.addInvitation(invitation);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInvitation(@PathVariable Integer id, @RequestBody @Valid InvitationIn invitation){
        invitationService.updateInvitation(id, invitation);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInvitation(@PathVariable Integer id){
        invitationService.deleteInvitation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Invitation deleted successfully"));
    }
}
