package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.HintModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HintRepository extends JpaRepository<HintModel, Integer> {

//    List<HintModel> findAllByGameSessionId(Integer gameSessionId);
}
