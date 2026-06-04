package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findAllByGameSessionId(Integer gameSessionId);
    List<Note> findAllByPlayerId(Integer playerId);
}
