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

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedCases() {
        return ResponseEntity.ok(caseService.getPublishedCases());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCase(@PathVariable Integer id, @RequestBody @Valid CaseIn dto) {
        caseService.updateCase(id, dto);
        return ResponseEntity.ok(new ApiResponse("Case updated successfully"));
    }

    @DeleteMapping("/delete/{adminId}/{caseId}")
    public ResponseEntity<?> deleteCase(@PathVariable Integer adminId,
                                        @PathVariable Integer caseId,
                                        @RequestBody @Valid AdminVerifyIn dto) {
        caseService.deleteCase(adminId, dto.getPassword(), caseId);
        return ResponseEntity.ok(new ApiResponse("Case deleted successfully"));
    }


    //for admin endpoints

    @PatchMapping("/publish/{adminId}/{caseId}")
    public ResponseEntity<?> publishCase(@PathVariable Integer adminId,
                                         @PathVariable Integer caseId,@RequestBody @Valid AdminVerifyIn dto) {
        caseService.publishCase(adminId, dto.getPassword(), caseId);
        return ResponseEntity.ok(new ApiResponse("Case published successfully"));
    }

    @PatchMapping("/draft/{adminId}/{caseId}")
    public ResponseEntity<?> moveCaseToDraft(@PathVariable Integer adminId, @PathVariable Integer caseId,
                                             @RequestBody @Valid AdminVerifyIn dto) {
        caseService.moveCaseToDraft(adminId, dto.getPassword(), caseId);
        return ResponseEntity.ok(new ApiResponse("Case moved back to DRAFT successfully"));
    }


    //AI endpoint


    @PostMapping("/generate/{adminId}")
    public ResponseEntity<?> generateCase(@PathVariable Integer adminId,
                                          @RequestBody @Valid AdminVerifyIn dto) {
        openAiService.generateCase(adminId, dto.getPassword());
        return ResponseEntity.status(201).body(new ApiResponse("Case generated successfully as DRAFT"));
    }
    @PostMapping("/generate-and-publish/{adminId}")
    public ResponseEntity<?> generateAndPublishCase(@PathVariable Integer adminId,
                                                    @RequestBody @Valid AdminVerifyIn dto) {
        openAiService.generateAndPublishCase(adminId, dto.getPassword());
        return ResponseEntity.status(201).body(new ApiResponse("Case generated and published successfully"));
    }
}
