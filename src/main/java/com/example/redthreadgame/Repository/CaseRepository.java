package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CaseRepository extends JpaRepository<Case, Integer> {
    Case findCaseById(Integer id);

    List<Case> findCasesByStatus(String status);


   // List<Case> findCasesByAdminId(Integer adminId);

}