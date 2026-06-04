package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
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

    @PostMapping("/add/{adminId}")
    public ResponseEntity<ApiResponse> addCase(@PathVariable Integer adminId, @RequestBody @Valid CaseIn dto) {
        caseService.addCase(adminId, dto);
        return ResponseEntity.status(201).body(new ApiResponse("Case added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCase(@PathVariable Integer id, @RequestBody @Valid CaseIn dto) {
        caseService.updateCase(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Case updated successfully"));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCase(@PathVariable Integer id) {
        caseService.deleteCase(id);
        return ResponseEntity.ok(new ApiResponse("Case deleted successfully"));
    }
    //سبب اختياره  لان نعدل على الستاتس
    @PatchMapping("/publish/{id}")
    public ResponseEntity<?> publishCase(@PathVariable Integer id) {
        caseService.publishCase(id);
        return ResponseEntity.ok(new ApiResponse("Case published successfully"));

    }

    @PostMapping("/generate/{adminId}")
    public ResponseEntity<?> generateCase(@PathVariable Integer adminId) {
        openAiService.generateCase(adminId);
        return ResponseEntity.status(200).body(new ApiResponse("Case generated successfully as DRAFT"));
    }

    @PostMapping("/generate-and-publish/{adminId}")
    public ResponseEntity<?> generateAndPublishCase(@PathVariable Integer adminId) {
        openAiService.generateAndPublishCase(adminId);
        return ResponseEntity.status(200).body(new ApiResponse("Case generated and published successfully"));
    }

}
