package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.EvidenceIn;
import com.example.redthreadgame.DTO.OUT.EvidenceOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.Evidence;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Suspect;
import com.example.redthreadgame.Repository.EvidenceRepository;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.SuspectRepository;
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
    private final CaseService caseService;
    private final SuspectRepository suspectRepository;
    private final GameSessionRepository gameSessionRepository;
    private final OpenAiService openAiService;


    public List<EvidenceOut> getAllEvidences() {
        List<EvidenceOut> evidences = new ArrayList<>();
        for (Evidence e : evidenceRepository.findAll()) {
            evidences.add(modelMapper.map(e, EvidenceOut.class));
        }
        return evidences;
    }
    public void addEvidence(Integer caseId, EvidenceIn dto) {
        Case c = caseService.checkCase(caseId);
        Evidence evidence = modelMapper.map(dto, Evidence.class);
        evidence.setEvidenceCase(c);
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

    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    public List<EvidenceOut> getEvidencesDetails(Integer caseId) {
        caseService.checkCase(caseId);
        List<EvidenceOut> evidences = new ArrayList<>();
        for (Evidence e : evidenceRepository.findEvidencesByEvidenceCaseId(caseId)) {
            evidences.add(modelMapper.map(e, EvidenceOut.class));
        }
        return evidences;
    }

    //link evidence to suspect
    public String linkEvidenceToSuspect(Integer evidenceId, Integer suspectId, Integer gameSessionId) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null) throw new ApiException("Game session not found");
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        Evidence evidence = checkEvidence(evidenceId);
        if (!evidence.getEvidenceCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Evidence does not belong to this case");

        Suspect suspect = suspectRepository.findSuspectById(suspectId);
        if (suspect == null) throw new ApiException("Suspect not found");
        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this case");

        String prompt = """
        You are a detective analyst.
        
        Case scenario: %s
        
        Evidence:
        Title: %s
        Description: %s
        
        Suspect:
        Name: %s
        Age: %s
        
        Analyze whether this evidence incriminates or clears this suspect.
        
        Respond in this exact JSON format:
        {
          "verdict": "INCRIMINATING or CLEARING",
          "analysis": "detailed analysis of how this evidence relates to the suspect",
          "confidence": "HIGH or MEDIUM or LOW"
        }
        Return ONLY the JSON, no extra text.
        """.formatted(gameSession.getSessionCase().getScenario(), evidence.getTitle(), evidence.getDescription(), suspect.getName(), suspect.getAge());

        String result = openAiService.generateAnswer(prompt);
        return result.trim().replace("```json", "").replace("```", "").trim();
    }

    //helper method
    private Evidence checkEvidence(Integer id) {
        Evidence evidence = evidenceRepository.findEvidenceById(id);
        if (evidence == null)
            throw new ApiException("Evidence not found");
        return evidence;
    }
}
