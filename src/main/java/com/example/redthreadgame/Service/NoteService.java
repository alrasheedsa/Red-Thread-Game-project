package com.example.redthreadgame.Service;
import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.NoteIn;
import com.example.redthreadgame.DTO.OUT.NoteOut;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Note;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.NoteRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;

    public List<NoteOut> getAllNotes() {
        List<NoteOut> notes = new ArrayList<>();

        for (Note n : noteRepository.findAll()) {
            notes.add(modelMapper.map(n, NoteOut.class));
        }
        return notes;
    }

    public List<NoteOut> getNotesByGameSession(Integer gameSessionId) {
        List<NoteOut> notes = new ArrayList<>();

        for (Note n : noteRepository.findAllByGameSessionId(gameSessionId)) {
            notes.add(modelMapper.map(n, NoteOut.class));
        }

        return notes;
    }

    public void addNote(Integer gameSessionId, Integer playerId, NoteIn dto) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        Note note = modelMapper.map(dto, Note.class);
        note.setGameSession(gameSession);
        note.setPlayer(player);

        noteRepository.save(note);
    }// Link the note to both the current game session and the player who wrote it

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
    public List<NoteOut> getLatestNotesBySession(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        List<NoteOut> notes = new ArrayList<>();

        for (Note n : noteRepository.findAllByGameSessionId(gameSessionId)) {
            notes.add(modelMapper.map(n, NoteOut.class));
        }

        notes.sort((n1, n2) -> n2.getId().compareTo(n1.getId()));
        return notes;
    }// Return session notes from newest to oldest so player can quickly review recent investigation updates

    public List<NoteOut> searchNotesBySession(Integer gameSessionId, String keyword) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        if (keyword == null || keyword.isBlank())
            throw new ApiException("Keyword is required");

        List<NoteOut> notes = new ArrayList<>();

        for (Note n : noteRepository.findAllByGameSessionId(gameSessionId)) {
            if (n.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                notes.add(modelMapper.map(n, NoteOut.class));
            }
        }

        return notes;
    }// Search only inside this session notes to help players find clues or suspect names
}
