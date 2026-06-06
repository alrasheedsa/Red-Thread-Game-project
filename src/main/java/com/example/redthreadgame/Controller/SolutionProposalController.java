package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.SolutionProposalIn;
import com.example.redthreadgame.Service.SolutionProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/solution-proposal")
@RestController
@RequiredArgsConstructor
public class SolutionProposalController {

    private final SolutionProposalService solutionProposalService;

    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getProposalsByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(solutionProposalService.getProposalsByGameSession(gameSessionId));
    }

    @PostMapping("/submit/{gameSessionId}/{playerId}/{suspectId}")
    public ResponseEntity<?> submitProposal(@PathVariable Integer gameSessionId, @PathVariable Integer playerId, @PathVariable Integer suspectId, @RequestBody @Valid SolutionProposalIn dto) {
        solutionProposalService.submitProposal(gameSessionId, playerId, suspectId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal submitted successfully"));
    }
    @GetMapping("/active/{gameSessionId}")
    public ResponseEntity<?> getActiveProposalBySession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(solutionProposalService.getActiveProposalBySession(gameSessionId));
    }
    @GetMapping("/result/{gameSessionId}")
    public ResponseEntity<?> getLastProposalResultBySession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(solutionProposalService.getLastProposalResultBySession(gameSessionId));
    }
}