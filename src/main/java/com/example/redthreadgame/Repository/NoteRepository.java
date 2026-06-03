package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Integer> {

//    List<NoteModel> findAllByGameSessionId(Integer gameSessionId);
}
