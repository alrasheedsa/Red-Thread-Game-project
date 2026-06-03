package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Witness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WitnessRepository extends JpaRepository<Witness, Integer> {

    Witness findWitnessById(Integer id);

    // جلب كل شهود قضية معينة
    List<Witness> findWitnessesByWitnessCaseId(Integer caseId);
}