package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.InvitationIn;
import com.example.redthreadgame.DTO.OUT.InvitationOut;
import com.example.redthreadgame.Model.Invitation;
import com.example.redthreadgame.Repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final ModelMapper modelMapper;
    private final InvitationRepository invitationRepository;


    //BASIC CRUD
    public List<InvitationOut> getAllInvitations(){
        List<InvitationOut> invitations = new ArrayList<>();
        for(Invitation i: invitationRepository.findAll()){
            invitations.add(modelMapper.map(i, InvitationOut.class));
        }
        return invitations;
    }

    public void addInvitation(InvitationIn invitationIn){
        Invitation invitation = modelMapper.map(invitationIn, Invitation.class);
        invitation.setStatus("PENDING");

        invitationRepository.save(invitation);
    }

    public void updateInvitation(Integer id, InvitationIn invitation){
        Invitation oldInvitation = checkInvitation(id);

        //??

        invitationRepository.save(oldInvitation);
    }

    public void deleteInvitation(Integer id){
        Invitation invitation = checkInvitation(id);
        invitationRepository.delete(invitation);
    }


    //HELPER METHODS
    private Invitation checkInvitation(Integer id){
        Invitation invitation = invitationRepository.findInvitationById(id);
        if(invitation == null) throw new ApiException("Invitation not found"); //check invitation

        return invitation;
    }
}
