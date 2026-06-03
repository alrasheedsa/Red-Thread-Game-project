package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Suspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspectRepository extends JpaRepository<Suspect, Integer> {

    Suspect findSuspectById(Integer id);
    //
    List<Suspect> findSuspectsBySuspectCaseId(Integer caseId);
    }
