package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.PlayerIn;
import com.example.redthreadgame.DTO.OUT.GameSessionOut;
import com.example.redthreadgame.DTO.OUT.InvitationOut;
import com.example.redthreadgame.DTO.OUT.PlayerOut;
import com.example.redthreadgame.Model.Invitation;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Repository.InvitationRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final ModelMapper modelMapper;
    private final PlayerRepository playerRepository;
    private final InvitationRepository invitationRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final OpenAiService openAiService;


    //BASIC CRUD
    public List<PlayerOut> getAllPlayers(){
        List<PlayerOut> players = new ArrayList<>();
        for(Player p: playerRepository.findAll()){
            players.add(modelMapper.map(p, PlayerOut.class));
        }
        return players;
    }

    public void addPlayer(PlayerIn playerIn){
        Player player = modelMapper.map(playerIn, Player.class);
        playerRepository.save(player);
    }

    public void updatePlayer(Integer id, PlayerIn playerIn){
        Player oldPlayer = checkPlayer(id);

        oldPlayer.setName(playerIn.getName());
        oldPlayer.setUsername(playerIn.getUsername());
        oldPlayer.setEmail(playerIn.getEmail());
        oldPlayer.setPhoneNumber(playerIn.getPhoneNumber());
        oldPlayer.setPassword(playerIn.getPassword());
        oldPlayer.setAge(playerIn.getAge());

        playerRepository.save(oldPlayer);
    }

    public void deletePlayer(Integer id){
        Player player = checkPlayer(id);
        playerRepository.delete(player);
    }


    //ِEXTRA ENDPOINTS
    public List<InvitationOut> getMyInvitations(Integer playerId){
        checkPlayer(playerId);
        List<InvitationOut> invitations = new ArrayList<>();
        for(Invitation i: invitationRepository.findAllByPlayerId(playerId)){
            invitations.add(modelMapper.map(i, InvitationOut.class));
        }
        return invitations;
    }

    public List<GameSessionOut> getMyGameSessions(Integer playerId){
        checkPlayer(playerId);
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(SessionPlayer s: sessionPlayerRepository.findAllByPlayerId(playerId)){
            gameSessions.add(modelMapper.map(s.getGameSession(), GameSessionOut.class));
        }
        return gameSessions;
    }

    public String getPlayerAnalysis(Integer playerId) {
        Player player = checkPlayer(playerId);
        List<SessionPlayer> sessionPlayers = sessionPlayerRepository.findAllByPlayerId(playerId);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this player's performance and give a short personal report with strengths, weaknesses, and recommendations:\n\n");
        prompt.append("Player: ").append(player.getName()).append("\n");
        prompt.append("Games Played: ").append(sessionPlayers.size()).append("\n");
        prompt.append("Cases:\n");

        for(SessionPlayer sp : sessionPlayers){
            prompt.append("- ").append(sp.getGameSession().getSessionCase().getTitle())
                    .append(" | Status: ").append(sp.getGameSession().getStatus())
                    .append(" | Role: ").append(sp.getRole()).append("\n");
        }

        return openAiService.analyzePlayer(prompt.toString());
    }

    public List<PlayerOut> getLeaderboard(){
        List<PlayerOut> players = new ArrayList<>();
        for(Player p: playerRepository.findAllByOrderByScoreDesc()){
            players.add(modelMapper.map(p, PlayerOut.class));
        }
        return players;
    }


    //HELPER METHODS
    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("Player not found"); //check player

        return player;
    }
}
