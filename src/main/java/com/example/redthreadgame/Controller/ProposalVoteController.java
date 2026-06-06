package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.ProposalVoteIn;
import com.example.redthreadgame.Service.ProposalVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/proposal-vote")
@RestController
@RequiredArgsConstructor
public class ProposalVoteController {

    private final ProposalVoteService proposalVoteService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllProposalVotes() {
        return ResponseEntity.status(200).body(proposalVoteService.getAllProposalVotes());
    }
    @PostMapping("/add/{proposalId}/{playerId}")
    public ResponseEntity<?> addProposalVote(@PathVariable Integer proposalId, @PathVariable Integer playerId, @RequestBody @Valid ProposalVoteIn dto) {
        proposalVoteService.addProposalVote(proposalId, playerId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Proposal vote added successfully"));
    }

    @GetMapping("/get-by-proposal/{proposalId}")
    public ResponseEntity<?> getVotesByProposal(@PathVariable Integer proposalId) {
        return ResponseEntity.status(200).body(proposalVoteService.getVotesByProposal(proposalId));
    }
}
