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

    @PutMapping("/accept/{proposalId}")
    public ResponseEntity<?> acceptProposal(@PathVariable Integer proposalId) {
        solutionProposalService.acceptProposal(proposalId);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal accepted successfully"));
    }

    @PutMapping("/reject/{proposalId}")
    public ResponseEntity<?> rejectProposal(@PathVariable Integer proposalId) {
        solutionProposalService.rejectProposal(proposalId);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal rejected successfully"));
    }

    @PutMapping("/status/{proposalId}")
    public ResponseEntity<?> changeStatus(@PathVariable Integer proposalId, @RequestParam String status) {
        solutionProposalService.changeStatus(proposalId, status);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal status changed successfully"));
    }
    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getProposalsByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(solutionProposalService.getProposalsByPlayer(playerId));
    }

    @PutMapping("/correct/{proposalId}")
    public ResponseEntity<?> markProposalCorrect(@PathVariable Integer proposalId) {
        solutionProposalService.markProposalCorrect(proposalId);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal marked correct successfully"));
    }

    @PutMapping("/wrong/{proposalId}")
    public ResponseEntity<?> markProposalWrong(@PathVariable Integer proposalId) {
        solutionProposalService.markProposalWrong(proposalId);
        return ResponseEntity.status(200).body(new ApiResponse("Solution proposal marked wrong successfully"));
    }
//    @PutMapping("/evaluate/{proposalId}")
//    public ResponseEntity<?> evaluateProposal(@PathVariable Integer proposalId) {
//        solutionProposalService.evaluateProposal(proposalId);
//    }
}