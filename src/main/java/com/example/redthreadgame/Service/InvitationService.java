package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.InvitationIn;
import com.example.redthreadgame.DTO.OUT.InvitationOut;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Invitation;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.InvitationRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
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
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;


    //BASIC CRUD
    public List<InvitationOut> getAllInvitations(){
        List<InvitationOut> invitations = new ArrayList<>();
        for(Invitation i: invitationRepository.findAll()){
            invitations.add(modelMapper.map(i, InvitationOut.class));
        }
        return invitations;
    }

    public void addInvitation(Integer gameSessionId, Integer playerId, InvitationIn invitationIn){
        GameSession gameSession = checkGameSession(gameSessionId);
//        Player owner = checkPlayer(ownerId);
//        if(owner != gameSession)

        Player player = checkPlayer(playerId);
        Invitation invitation = modelMapper.map(invitationIn, Invitation.class);
        invitation.setGameSession(gameSession);
        invitation.setPlayer(player);
        invitation.setStatus("PENDING");

        invitationRepository.save(invitation);
    }

    public void deleteInvitation(Integer id){
        Invitation invitation = checkInvitation(id);
        invitationRepository.delete(invitation);
    }


    //EXTRA ENDPOINTS
    public void updateStatus(Integer id, String status){
        if(!status.equalsIgnoreCase("PENDING") && !status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED"))
            throw new ApiException("status must be 'PENDING', 'ACCEPTED' or 'REJECTED' only");

        Invitation invitation = checkInvitation(id);
        if(!invitation.getStatus().equalsIgnoreCase("PENDING"))
            throw new ApiException(invitation.getStatus() + " cann't change");
    }


    //HELPER METHODS
    private Invitation checkInvitation(Integer id){
        Invitation invitation = invitationRepository.findInvitationById(id);
        if(invitation == null) throw new ApiException("Invitation not found"); //check invitation

        return invitation;
    }

    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }

    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("Player not found"); //check player

        return player;
    }
}
