package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.NoteIn;
import com.example.redthreadgame.DTO.OUT.NoteOut;
import com.example.redthreadgame.Service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/note")
@RestController
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllNotes() {
        return ResponseEntity.status(200).body(noteService.getAllNotes());
    }

    @PostMapping("/add/{gameSessionId}/{playerId}")
    public ResponseEntity<?> addNote(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @RequestBody @Valid NoteIn dto) {
        noteService.addNote(gameSessionId, playerId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Note added successfully"));
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable Integer noteId, @RequestBody @Valid NoteIn dto) {
        noteService.updateNote(noteId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Note updated successfully"));
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Integer noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.status(200).body(new ApiResponse("Note deleted successfully"));
    }

    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getNotesByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(noteService.getNotesByGameSession(gameSessionId));
    }

    @GetMapping("/latest/{gameSessionId}")
    public ResponseEntity<?> getLatestNotesBySession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(noteService.getLatestNotesBySession(gameSessionId));
    }
    @GetMapping("/search/{gameSessionId}")
    public ResponseEntity<?> searchNotesBySession(@PathVariable Integer gameSessionId, @RequestParam String keyword) {
        return ResponseEntity.status(200).body(noteService.searchNotesBySession(gameSessionId, keyword));
    }
}
