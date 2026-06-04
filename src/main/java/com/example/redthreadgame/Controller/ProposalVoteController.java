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

    @GetMapping("/get-by-proposal/{proposalId}")
    public ResponseEntity<?> getVotesByProposal(@PathVariable Integer proposalId) {
        return ResponseEntity.status(200).body(proposalVoteService.getVotesByProposal(proposalId));
    }

    @GetMapping("/get-by-player/{playerId}")
    public ResponseEntity<?> getVotesByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(proposalVoteService.getVotesByPlayer(playerId));
    }

    @PostMapping("/add/{proposalId}/{playerId}")
    public ResponseEntity<?> addProposalVote(@PathVariable Integer proposalId, @PathVariable Integer playerId, @RequestBody @Valid ProposalVoteIn dto) {
        proposalVoteService.addProposalVote(proposalId, playerId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Proposal vote added successfully"));
    }

    @PutMapping("/update/{voteId}")
    public ResponseEntity<?> updateProposalVote(@PathVariable Integer voteId, @RequestBody @Valid ProposalVoteIn dto) {
        proposalVoteService.updateProposalVote(voteId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Proposal vote updated successfully"));
    }

    @DeleteMapping("/delete/{voteId}")
    public ResponseEntity<?> deleteProposalVote(@PathVariable Integer voteId) {
        proposalVoteService.deleteProposalVote(voteId);
        return ResponseEntity.status(200).body(new ApiResponse("Proposal vote deleted successfully"));
    }
    @GetMapping("/majority-accepted/{proposalId}")
    public ResponseEntity<?> hasMajorityAccepted(@PathVariable Integer proposalId) {
        return ResponseEntity.status(200).body(proposalVoteService.hasMajorityAccepted(proposalId));
    }

    @GetMapping("/majority-rejected/{proposalId}")
    public ResponseEntity<?> hasMajorityRejected(@PathVariable Integer proposalId) {
        return ResponseEntity.status(200).body(proposalVoteService.hasMajorityRejected(proposalId));
    }
}
