package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.CaseSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseSolutionRepository extends JpaRepository<CaseSolution, Integer> {
    CaseSolution findCaseSolutionById(Integer caseId);

}
