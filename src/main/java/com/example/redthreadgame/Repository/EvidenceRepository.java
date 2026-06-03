package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Integer> {

    Evidence findEvidenceById(Integer id);
    //all evidence by case
    //List<Evidence> findEvidencesByCaseEntityId(Integer caseId);
}
