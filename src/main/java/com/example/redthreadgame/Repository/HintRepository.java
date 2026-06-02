package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Hint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HintRepository extends JpaRepository<Hint, Integer> {

//    List<Hint> findAllByGameSessionId(Integer gameSessionId);
}
