package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Invitation;
import com.example.redthreadgame.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    Invitation findInvitationById(Integer id);

    List<Invitation> findAllByPlayerId(Integer playerId);

    boolean existsByGameSessionAndPlayer(GameSession gameSession, Player player);

    @Query("SELECT i FROM Invitation i WHERE i.gameSession.id = ?1 AND i.player.id = ?2")
    Invitation findByGameSessionIdAndPlayerId(Integer gameSessionId, Integer playerId);
}
