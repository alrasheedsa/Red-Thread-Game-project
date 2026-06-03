package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.SolutionProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionProposalRepository extends JpaRepository<SolutionProposal, Integer> {

    List<SolutionProposal> findAllByGameSessionId(Integer gameSessionId);
}
