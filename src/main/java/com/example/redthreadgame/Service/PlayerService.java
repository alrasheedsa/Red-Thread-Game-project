package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.PlayerIn;
import com.example.redthreadgame.DTO.OUT.PlayerOut;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Repository.PlayerRepository;
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
        oldPlayer.setPhoneNumber(oldPlayer.getPhoneNumber());
        oldPlayer.setPassword(playerIn.getPassword());
        oldPlayer.setAge(playerIn.getAge());

        playerRepository.save(oldPlayer);
    }

    public void deletePlayer(Integer id){
        Player player = checkPlayer(id);
        playerRepository.delete(player);
    }


    //HELPER METHODS
    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("Player not found"); //check player

        return player;
    }
}
