package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {

    GameSession findGameSessionById (Integer id);
}
