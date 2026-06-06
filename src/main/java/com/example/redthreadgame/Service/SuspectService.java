package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.SuspectIn;
import com.example.redthreadgame.DTO.OUT.SuspectOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.QuestionTargetType;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.SuspectRepository;
import com.example.redthreadgame.Repository.WitnessRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuspectService {
    private final ModelMapper modelMapper;
    private final SuspectRepository suspectRepository;
    private final CaseService caseService;
    private final OpenAiService openAiService;
    private final GameSessionRepository gameSessionRepository;
    private final WitnessRepository witnessRepository;

  public List<SuspectOut> getAllSuspects() {
      List<SuspectOut> suspects = new ArrayList<>();
      for (Suspect s : suspectRepository.findAll()) {
          suspects.add(modelMapper.map(s, SuspectOut.class));
      }
      return suspects;
  }
    public void addSuspect(Integer caseId, SuspectIn dto) {
         Case c = caseService.checkCase(caseId);
        Suspect suspect = modelMapper.map(dto, Suspect.class);
        suspect.setSuspectCase(c);

        suspectRepository.save(suspect);
    }
    public void updateSuspect(Integer id, SuspectIn dto) {
        Suspect old = checkSuspect(id);
        old.setName(dto.getName());
        old.setAge(dto.getAge());
        old.setGender(dto.getGender());
        old.setVoiceTone(dto.getVoiceTone());

        suspectRepository.save(old);
    }
    public void deleteSuspect(Integer id) {
        suspectRepository.delete(checkSuspect(id));
    }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    public List<SuspectOut> getSuspectsDetails(Integer caseId) {
        caseService.checkCase(caseId);
        List<SuspectOut> suspects = new ArrayList<>();
        for (Suspect s : suspectRepository.findSuspectsBySuspectCaseId(caseId)) {
            suspects.add(modelMapper.map(s, SuspectOut.class));
        }
        return suspects;
    }

    public String confrontSuspectWithWitness(Integer suspectId, Integer witnessId, Integer gameSessionId) {
        // تحقق من الجلسة
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null) throw new ApiException("Game session not found");
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        // تحقق من المشتبه به والشاهد
        Suspect suspect = checkSuspect(suspectId);
        Witness witness = witnessRepository.findWitnessById(witnessId);
        if (witness == null) throw new ApiException("Witness not found");

        // تحقق إنهم من نفس القضية
        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this case");
        if (!witness.getWitnessCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Witness does not belong to this case");

        String prompt = """
            You are directing a tense confrontation scene in a detective mystery game.
            
            Case scenario: %s
            
            Witness: %s
            Witness statement: %s
            
            Suspect: %s
            Suspect age: %s
            
            Create a short dramatic dialogue confrontation between them.
            The witness presses the suspect with what they saw.
            The suspect defends themselves naturally based on their voice tone: %s
            Do NOT reveal who is guilty.
            Keep it tense, realistic, and useful for the investigation.
            
            Respond in this exact JSON format:
            {
              "dialogue": [
                {"speaker": "Witness - name", "line": "what they say"},
                {"speaker": "Suspect - name", "line": "what they say"},
                {"speaker": "Witness - name", "line": "what they say"},
                {"speaker": "Suspect - name", "line": "what they say"}
              ],
              "tension": "HIGH or MEDIUM or LOW",
              "outcome": "one sentence describing what this confrontation revealed"
            }
            Return ONLY the JSON, no extra text.
            """.formatted(
                gameSession.getSessionCase().getScenario(),
                witness.getName(),
                witness.getStatement(),
                suspect.getName(),
                suspect.getAge(),
                suspect.getVoiceTone()
        );

        String result = openAiService.generateAnswer(prompt);
        return result.trim().replace("```json", "").replace("```", "").trim();
    }

    public List<SuspectOut> getNotQuestionedSuspects(Integer gameSessionId) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null) throw new ApiException("Game session not found");

        // كل مشتبهين القضية
        List<Suspect> allSuspects = suspectRepository
                .findSuspectsBySuspectCaseId(gameSession.getSessionCase().getId());

        // ids المشتبهين اللي تم سؤالهم في هذه الجلسة
        List<Integer> questionedIds = new ArrayList<>();
        for (Question q : gameSession.getQuestions()) {
            if (q.getTargetType() == QuestionTargetType.SUSPECT && q.getSuspect() != null) {
                questionedIds.add(q.getSuspect().getId());
            }
        }

        // حذف اللي تم سؤالهم
        List<SuspectOut> notQuestioned = new ArrayList<>();
        for (Suspect s : allSuspects) {
            if (!questionedIds.contains(s.getId())) {
                notQuestioned.add(modelMapper.map(s, SuspectOut.class));
            }
        }

        if (notQuestioned.isEmpty())
            throw new ApiException("All suspects have been questioned");

        return notQuestioned;
    }

    //helper method
    public Suspect checkSuspect(Integer id) {
        Suspect suspect = suspectRepository.findSuspectById(id);
        if (suspect == null) throw new ApiException("Suspect not found");
        return suspect;
    }


}
