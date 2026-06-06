package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Player findPlayerById (Integer id);

    List<Player> findAllByOrderByScoreDesc();
}
