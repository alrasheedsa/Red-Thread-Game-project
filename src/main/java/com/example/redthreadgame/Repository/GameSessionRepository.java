package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {

    GameSession findGameSessionById (Integer id);

    GameSession findBySessionCode(String sessionCode);

    boolean existsBySessionCode(String sessionCode);

    List<GameSession> findAllByIsPrivateFalse();

    List<GameSession> findAllByStatus(GameSessionStatusType status);

    List<GameSession> findAllByIsPrivateFalseAndSessionCaseIdAndStatus(Integer caseId, GameSessionStatusType status);
}
