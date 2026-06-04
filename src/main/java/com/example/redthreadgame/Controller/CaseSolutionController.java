package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.CaseSolutionIn;
import com.example.redthreadgame.Service.CaseSolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/case-solutions")
@RequiredArgsConstructor
public class CaseSolutionController {
    private final CaseSolutionService caseSolutionService;

    @GetMapping("/get/{caseId}")
    public ResponseEntity<?> getCaseSolution(@PathVariable Integer caseId) {
        return ResponseEntity.ok(caseSolutionService.getCaseSolution(caseId));
    }

    @PostMapping("/add/{caseId}")
    public ResponseEntity<ApiResponse> addCaseSolution(@PathVariable Integer caseId, @RequestBody @Valid CaseSolutionIn dto) {
        caseSolutionService.addCaseSolution(caseId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Case solution added successfully"));
    }
@PutMapping("/update/{caseId}")
public ResponseEntity<?> updateCaseSolution(@PathVariable Integer caseId, @RequestBody @Valid CaseSolutionIn dto) {
    caseSolutionService.updateCaseSolution(caseId, dto);
    return ResponseEntity.ok(new ApiResponse("Case solution updated successfully"));
}
    @DeleteMapping("/delete/{caseId}")
    public ResponseEntity<?> deleteCaseSolution(@PathVariable Integer caseId) {
        caseSolutionService.deleteCaseSolution(caseId);
        return ResponseEntity.ok(new ApiResponse("Case solution deleted successfully"));

    }

}