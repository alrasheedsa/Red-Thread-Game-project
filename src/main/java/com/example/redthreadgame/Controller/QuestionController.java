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

    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getQuestionsByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsByGameSession(gameSessionId));
    }

    @GetMapping("/get-by-player/{playerId}")
    public ResponseEntity<?> getQuestionsByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsByPlayer(playerId));
    }

    @PostMapping("/add-witness/{gameSessionId}/{playerId}/{witnessId}")
    public ResponseEntity<?> addWitnessQuestion(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @PathVariable Integer witnessId, @RequestBody @Valid QuestionIn dto) {
        questionService.addWitnessQuestion(gameSessionId, playerId, witnessId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Witness question added successfully"));
    }

    @PostMapping("/add-suspect/{gameSessionId}/{playerId}/{suspectId}")
    public ResponseEntity<?> addSuspectQuestion(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @PathVariable Integer suspectId, @RequestBody @Valid QuestionIn dto) {
        questionService.addSuspectQuestion(gameSessionId, playerId, suspectId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Suspect question added successfully"));
    }

    @PutMapping("/update/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer questionId, @RequestBody @Valid QuestionIn dto) {
        questionService.updateQuestion(questionId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Question updated successfully"));
    }

    @DeleteMapping("/delete/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.status(200).body(new ApiResponse("Question deleted successfully"));
    }
    @GetMapping("/witness/{witnessId}")
    public ResponseEntity<?> getQuestionsByWitness(@PathVariable Integer witnessId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsByWitnessId(witnessId));
    }
    @GetMapping("/suspect/{suspectId}")
    public ResponseEntity<?> getQuestionsBySuspect(@PathVariable Integer suspectId) {
        return ResponseEntity.status(200).body(questionService.getQuestionsBySuspectId(suspectId));
    }

}
