package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.EvidenceIn;
import com.example.redthreadgame.DTO.OUT.EvidenceOut;
import com.example.redthreadgame.Model.Evidence;
import com.example.redthreadgame.Repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final ModelMapper modelMapper;
    private final EvidenceRepository evidenceRepository;
    // private final CaseService caseService;

    public List<EvidenceOut> getAllEvidences() {
        List<EvidenceOut> evidences = new ArrayList<>();
        for (Evidence e : evidenceRepository.findAll()) {
            evidences.add(modelMapper.map(e, EvidenceOut.class));
        }
        return evidences;
    }

    public void addEvidence(Integer caseId, EvidenceIn dto) {
        // Case c = caseService.checkCase(caseId);
        Evidence evidence = modelMapper.map(dto, Evidence.class);
        // evidence.setCaseEntity(c);
        evidenceRepository.save(evidence);
    }

    public void updateEvidence(Integer id, EvidenceIn dto) {
        Evidence old = checkEvidence(id);
        old.setTitle(dto.getTitle());
        old.setDescription(dto.getDescription());
        evidenceRepository.save(old);
    }

    public void deleteEvidence(Integer id) {
        evidenceRepository.delete(checkEvidence(id));
    }

    // public List<EvidenceOut> getEvidencesByCaseId(Integer caseId) {
    //     caseService.checkCase(caseId);
    //     List<EvidenceOut> evidences = new ArrayList<>();
    //     for (Evidence e : evidenceRepository.findEvidencesByCaseEntityId(caseId)) {
    //         evidences.add(modelMapper.map(e, EvidenceOut.class));
    //     }
    //     return evidences;
    // }

    private Evidence checkEvidence(Integer id) {
        Evidence evidence = evidenceRepository.findEvidenceById(id);
        if (evidence == null) throw new ApiException("Evidence not found");
        return evidence;
    }
}
