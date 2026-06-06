package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.SuspectIn;
import com.example.redthreadgame.Service.SuspectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suspects")
@RequiredArgsConstructor
public class SuspectController {

    private final SuspectService suspectService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllSuspects() {
        return ResponseEntity.status(200).body(suspectService.getAllSuspects());
    }

    @PostMapping("/add/{caseId}")
    public ResponseEntity<?> addSuspect(@PathVariable Integer caseId, @RequestBody @Valid SuspectIn dto) {
        suspectService.addSuspect(caseId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Suspect added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateSuspect(@PathVariable Integer id, @RequestBody @Valid SuspectIn dto) {
        suspectService.updateSuspect(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Suspect updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteSuspect(@PathVariable Integer id) {
        suspectService.deleteSuspect(id);
        return ResponseEntity.status(200).body(new ApiResponse("Suspect deleted successfully"));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<?> getSuspectsDetails(@PathVariable Integer caseId) {
        return ResponseEntity.status(200).body(suspectService.getSuspectsDetails(caseId));
    }

    @PostMapping("/confront/{suspectId}/{witnessId}/{gameSessionId}")
    public ResponseEntity<?> confrontSuspectWithWitness(@PathVariable Integer suspectId, @PathVariable Integer witnessId, @PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(suspectService.confrontSuspectWithWitness(suspectId, witnessId, gameSessionId));
    }

    @GetMapping("/not-questioned/{gameSessionId}")
    public ResponseEntity<?> getNotQuestionedSuspects(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(suspectService.getNotQuestionedSuspects(gameSessionId));
    }
}
