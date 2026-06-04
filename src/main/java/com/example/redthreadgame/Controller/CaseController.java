package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.AdminVerifyIn;
import com.example.redthreadgame.DTO.IN.CaseIn;
import com.example.redthreadgame.Service.CaseService;
import com.example.redthreadgame.Service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private final OpenAiService openAiService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    //get just published cases
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedCases() {
        return ResponseEntity.status(200).body(caseService.getAllCases());
    }

    @PutMapping("/update/{adminId}/{caseId}") public ResponseEntity<?> updateCase(@PathVariable Integer adminId, @PathVariable Integer caseId, @RequestBody @Valid CaseIn dto) {
        caseService.updateCase(adminId, dto.getPassword(), caseId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Case updated successfully"));
    }

    @DeleteMapping("/delete/{adminId}/{caseId}")
    public ResponseEntity<?> deleteCase(@PathVariable Integer adminId, @PathVariable Integer caseId, @RequestBody @Valid AdminVerifyIn dto) {
        caseService.deleteCase(adminId, dto.getPassword(), caseId);
        return ResponseEntity.status(200).body(new ApiResponse("Case deleted successfully"));
    }
    //for admin endpoints
    @PutMapping("/publish/{adminId}/{caseId}")
    public ResponseEntity<?> publishCase(@PathVariable Integer adminId, @PathVariable Integer caseId,@RequestBody @Valid AdminVerifyIn dto) {
        caseService.publishCase(adminId, dto.getPassword(), caseId);
        return ResponseEntity.status(200).body(new ApiResponse("Case published successfully"));
    }

    @PutMapping("/draft/{adminId}/{caseId}")
    public ResponseEntity<?> moveCaseToDraft(@PathVariable Integer adminId, @PathVariable Integer caseId, @RequestBody @Valid AdminVerifyIn dto) {
        caseService.moveCaseToDraft(adminId, dto.getPassword(), caseId);
        return ResponseEntity.status(200).body(new ApiResponse("Case moved back to DRAFT successfully"));
    }

    //AI endpoint
    @PostMapping("/generate/{adminId}")
    public ResponseEntity<?> generateCase(@PathVariable Integer adminId, @RequestBody @Valid AdminVerifyIn dto) {
        openAiService.generateCase(adminId, dto.getPassword());
        return ResponseEntity.status(200).body(new ApiResponse("Case generated successfully as DRAFT"));
    }
}
