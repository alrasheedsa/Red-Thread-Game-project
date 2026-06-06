package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.Service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/question")
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllQuestions() {
        return ResponseEntity.status(200).body(questionService.getAllQuestions());
    }

    @PostMapping("/ask-witness/{gameSessionId}/{playerId}/{witnessId}")
    public ResponseEntity<?> askWitnessQuestion(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @PathVariable Integer witnessId, @RequestBody @Valid QuestionIn dto) {
        return ResponseEntity.status(200).body(questionService.askWitnessQuestion(gameSessionId, playerId, witnessId, dto));
    }

    @PostMapping("/ask-suspect/{gameSessionId}/{playerId}/{suspectId}")
    public ResponseEntity<?> askSuspectQuestion(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @PathVariable Integer suspectId, @RequestBody @Valid QuestionIn dto) {
        return ResponseEntity.status(200).body(questionService.askSuspectQuestion(gameSessionId, playerId, suspectId, dto));
    }
    @GetMapping("/count/{gameSessionId}")
    public ResponseEntity<?> getQuestionsCountBySession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsCountBySession(gameSessionId));
    }
    @GetMapping("/next-penalty/{gameSessionId}")
    public ResponseEntity<?> getNextQuestionPenalty(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(questionService.getNextQuestionPenalty(gameSessionId));
    }

    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getQuestionsByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsByGameSession(gameSessionId));
    }
}
