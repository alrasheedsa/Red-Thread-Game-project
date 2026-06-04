package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.ProposalVoteIn;
import com.example.redthreadgame.DTO.OUT.ProposalVoteOut;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.ProposalVote;
import com.example.redthreadgame.Enums.ProposalVoteType;
import com.example.redthreadgame.Model.SolutionProposal;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.ProposalVoteRepository;
import com.example.redthreadgame.Repository.SolutionProposalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalVoteService {

    private final ProposalVoteRepository proposalVoteRepository;
    private final SolutionProposalRepository solutionProposalRepository;
    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;

    public List<ProposalVoteOut> getAllProposalVotes() {
        List<ProposalVoteOut> votes = new ArrayList<>();

        for (ProposalVote p : proposalVoteRepository.findAll()) {
            votes.add(modelMapper.map(p, ProposalVoteOut.class));
        }

        return votes;
    }

    public List<ProposalVoteOut> getVotesByProposal(Integer proposalId) {
        List<ProposalVoteOut> votes = new ArrayList<>();

        for (ProposalVote p : proposalVoteRepository.findAllBySolutionProposalId(proposalId)) {
            votes.add(modelMapper.map(p, ProposalVoteOut.class));
        }

        return votes;
    }

    public List<ProposalVoteOut> getVotesByPlayer(Integer playerId) {
        List<ProposalVoteOut> votes = new ArrayList<>();

        for (ProposalVote p : proposalVoteRepository.findAllByPlayerId(playerId)) {
            votes.add(modelMapper.map(p, ProposalVoteOut.class));
        }

        return votes;
    }

    public void addProposalVote(Integer proposalId, Integer playerId, ProposalVoteIn dto) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        if (proposalVoteRepository.findProposalVoteBySolutionProposalIdAndPlayerId(proposalId, playerId) != null) {
            throw new ApiException("Player already voted on this proposal");
        }

        ProposalVoteType voteType = parseVote(dto.getVote());

        ProposalVote proposalVote = new ProposalVote();
        proposalVote.setVote(voteType);
        proposalVote.setSolutionProposal(proposal);
        proposalVote.setPlayer(player);
        proposalVote.setVotedAt(LocalDateTime.now());
        updateProposalVoteCount(proposal, voteType, 1);
        proposalVoteRepository.save(proposalVote);
        solutionProposalRepository.save(proposal);
    }

    public void updateProposalVote(Integer voteId, ProposalVoteIn dto) {
        ProposalVote proposalVote = checkProposalVote(voteId);
        ProposalVoteType newVote = parseVote(dto.getVote());
        ProposalVoteType oldVote = proposalVote.getVote();

        if (oldVote != newVote) {
            updateProposalVoteCount(proposalVote.getSolutionProposal(), oldVote, -1);
            updateProposalVoteCount(proposalVote.getSolutionProposal(), newVote, 1);
        }

        proposalVote.setVote(newVote);
        proposalVoteRepository.save(proposalVote);
        solutionProposalRepository.save(proposalVote.getSolutionProposal());
    }

    public void deleteProposalVote(Integer voteId) {
        ProposalVote proposalVote = checkProposalVote(voteId);
        SolutionProposal proposal = proposalVote.getSolutionProposal();

        updateProposalVoteCount(proposal, proposalVote.getVote(), -1);
        proposalVoteRepository.delete(proposalVote);
        solutionProposalRepository.save(proposal);
    }
    public Boolean hasMajorityAccepted(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        Integer playersCount = proposal.getGameSession().getPlayersCount();
        return proposal.getAcceptCount() > playersCount / 2;
    }

    public Boolean hasMajorityRejected(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        Integer playersCount = proposal.getGameSession().getPlayersCount();
        return proposal.getRejectCount() >= playersCount / 2;
    }

    private ProposalVote checkProposalVote(Integer id) {
        ProposalVote proposalVote = proposalVoteRepository.findProposalVoteById(id);
        if (proposalVote == null) throw new ApiException("Proposal vote not found");
        return proposalVote;
    }

    private ProposalVoteType parseVote(String vote) {
        try {
            return ProposalVoteType.valueOf(vote.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("vote must be 'ACCEPT' or 'REJECT' only");
        }
    }

    private void updateProposalVoteCount(SolutionProposal proposal, ProposalVoteType vote, Integer amount) {
        if (vote == ProposalVoteType.ACCEPT) {
            proposal.setAcceptCount(proposal.getAcceptCount() + amount);
        } else {
            proposal.setRejectCount(proposal.getRejectCount() + amount);
        }
    }
}
