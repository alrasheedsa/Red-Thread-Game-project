package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.ProposalVoteIn;
import com.example.redthreadgame.DTO.OUT.ProposalVoteOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.SolutionProposalStatusType;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.ProposalVote;
import com.example.redthreadgame.Enums.ProposalVoteType;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Model.SolutionProposal;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.ProposalVoteRepository;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
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
    private final SessionPlayerRepository sessionPlayerRepository;
    private final SolutionProposalService solutionProposalService;
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

    public void addProposalVote(Integer proposalId, Integer playerId, ProposalVoteIn dto) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        checkCanVote(proposal, player);

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
        updateProposalStatusByMajority(proposal);
        proposalVoteRepository.save(proposalVote);
        solutionProposalRepository.save(proposal);

        if (proposal.getStatus() == SolutionProposalStatusType.ACCEPTED_BY_PLAYERS)
            solutionProposalService.evaluateProposal(proposal.getId());
    }// Save player vote, updates proposal count, automatically evaluate the proposal when enough players accept

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

    private void checkCanVote(SolutionProposal proposal, Player player) {
        if (proposal.getGameSession().getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(proposal.getGameSession(), player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");

        if (proposal.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Proposal owner cannot vote on his own proposal");

        if (proposal.getStatus() != SolutionProposalStatusType.PENDING)
            throw new ApiException("You cannot vote on this proposal");
    }//wrong session state, nonjoined player, proposal owner voting, repeated or closed proposal voting

    private void updateProposalStatusByMajority(SolutionProposal proposal) {
        Integer requiredVotes = getRequiredVotes(proposal);
        if (proposal.getAcceptCount() >= requiredVotes)
            proposal.setStatus(SolutionProposalStatusType.ACCEPTED_BY_PLAYERS);
        else if (proposal.getRejectCount() >= requiredVotes)
            proposal.setStatus(SolutionProposalStatusType.REJECTED_BY_PLAYERS);
    }// Mark the proposal accepted or rejected once votes reach half of the session players or more

    private Integer getRequiredVotes(SolutionProposal proposal) {
        return (int) Math.ceil(proposal.getGameSession().getPlayersCount() / 2.0);
    }// Calculate how many votes are needed half of the session players rounded up
}
