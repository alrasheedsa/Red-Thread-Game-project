package com.example.redthreadgame.Service;
import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.NoteIn;
import com.example.redthreadgame.DTO.OUT.NoteOut;
import com.example.redthreadgame.Model.Note;
import com.example.redthreadgame.Repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
//    private final GameSessionRepository gameSessionRepository;
//    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;

    public List<NoteOut> getAllNotes() {
        List<NoteOut> notes = new ArrayList<>();

        for (Note n : noteRepository.findAll()) {
            notes.add(modelMapper.map(n, NoteOut.class));
        }

        return notes;
    }

//    public List<NoteOut> getNotesByGameSession(Integer gameSessionId) {
//        List<NoteOut> notes = new ArrayList<>();
//
//        for (Note n : noteRepository.findAllByGameSessionId(gameSessionId)) {
//            notes.add(modelMapper.map(n, NoteOut.class));
//        }
//
//        return notes;
//    }

//    public void addNote(Integer gameSessionId, Integer playerId, NoteIn dto) {
//        GameSessionModel gameSession = gameSessionRepository.findById(gameSessionId)
//                .orElseThrow(() -> new ApiException("Game session not found"));
//
//        PlayerModel player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new ApiException("Player not found"));
//
//        Note note = modelMapper.map(dto, Note.class);
//        note.setGameSession(gameSession);
//        note.setPlayer(player);
//
//        noteRepository.save(note);
//    }

    public void updateNote(Integer noteId, NoteIn dto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiException("Note not found"));

        note.setContent(dto.getContent());
        noteRepository.save(note);
    }

    public void deleteNote(Integer noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiException("Note not found"));

        noteRepository.delete(note);
    }
}
