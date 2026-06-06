package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.EvidenceIn;
import com.example.redthreadgame.Service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evidences")
@RequiredArgsConstructor
public class EvidenceController {
    private final EvidenceService evidenceService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllEvidences() {
        return ResponseEntity.status(200).body(evidenceService.getAllEvidences());
    }
    @PostMapping("/add/{caseId}")
    public ResponseEntity<?> addEvidence(@PathVariable Integer caseId, @RequestBody @Valid EvidenceIn dto) {
        evidenceService.addEvidence(caseId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Evidence added successfully"));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvidence(@PathVariable Integer id, @RequestBody @Valid EvidenceIn dto) {
        evidenceService.updateEvidence(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Evidence updated successfully"));}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvidence(@PathVariable Integer id) {
        evidenceService.deleteEvidence(id);
        return ResponseEntity.ok(new ApiResponse("Evidence deleted successfully"));
    }
    @GetMapping("/case/{caseId}")
    public ResponseEntity<?> getEvidencesDetails(@PathVariable Integer caseId) {
        return ResponseEntity.status(200).body(evidenceService.getEvidencesDetails(caseId));
    }

}
