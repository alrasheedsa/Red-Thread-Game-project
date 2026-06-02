package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.SolutionProposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionProposalRepository extends JpaRepository<SolutionProposal, Integer> {

//    List<SolutionProposalModel> findAllByGameSessionId(Integer gameSessionId);
}
