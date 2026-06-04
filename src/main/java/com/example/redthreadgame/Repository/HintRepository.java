package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Hint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HintRepository extends JpaRepository<Hint, Integer> {

    List<Hint> findAllByGameSessionId(Integer gameSessionId);
    List<Hint> findAllByPlayerId(Integer playerId);

}
