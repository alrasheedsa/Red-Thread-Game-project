package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.WitnessIn;
import com.example.redthreadgame.DTO.OUT.WitnessOut;
import com.example.redthreadgame.Model.Witness;
import com.example.redthreadgame.Repository.WitnessRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WitnessService {
    private final ModelMapper modelMapper;
    private final WitnessRepository witnessRepository;
    // private final CaseService caseService;

    public List<WitnessOut> getAllWitnesses() {
        List<WitnessOut> witnesses = new ArrayList<>();
        for (Witness w : witnessRepository.findAll()) {
            witnesses.add(modelMapper.map(w, WitnessOut.class));
        }
        return witnesses;
    }

    public void addWitness(Integer caseId, WitnessIn dto) {
        // Case c = caseService.checkCase(caseId);
        Witness witness = modelMapper.map(dto, Witness.class);
        // witness.setCaseEntity(c);
        witnessRepository.save(witness);
    }

    public void updateWitness(Integer id, WitnessIn dto) {
        Witness old = checkWitness(id);
        old.setName(dto.getName());
        old.setStatement(dto.getStatement());
        old.setReliabilityScore(dto.getReliabilityScore());
        witnessRepository.save(old);
    }

    public void deleteWitness(Integer id) {
        witnessRepository.delete(checkWitness(id));
    }
    // public List<WitnessOut> getWitnessesByCaseId(Integer caseId) {
    //     caseService.checkCase(caseId);
    //     List<WitnessOut> witnesses = new ArrayList<>();
    //     for (Witness w : witnessRepository.findWitnessesByCaseEntityId(caseId)) {
    //         witnesses.add(modelMapper.map(w, WitnessOut.class));
    //     }
    //     return witnesses;
    // }


    private Witness checkWitness(Integer id) {
        Witness witness = witnessRepository.findWitnessById(id);
        if (witness == null)
            throw new ApiException("Witness not found");
        return witness;
    }

}
