package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.IN.SuspectIn;
import com.example.redthreadgame.DTO.OUT.VoiceAnswerOut;
import com.example.redthreadgame.DTO.OUT.SuspectOut;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.Suspect;
import com.example.redthreadgame.Repository.SuspectRepository;
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
    private final ElevenLabsService elevenLabsService;
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

        suspectRepository.save(old);
    }

    public void deleteSuspect(Integer id) {
        suspectRepository.delete(checkSuspect(id));
    }

    //endpoint get suspect  by case
    public List<SuspectOut> getSuspectsDetails(Integer caseId) {
        caseService.checkCase(caseId);
        List<SuspectOut> suspects = new ArrayList<>();
        for (Suspect s : suspectRepository.findSuspectsBySuspectCaseId(caseId)) {
            suspects.add(modelMapper.map(s, SuspectOut.class));
        }
        return suspects;
    }

    public VoiceAnswerOut askSuspect(Integer suspectId, QuestionIn dto) {
        Suspect suspect = checkSuspect(suspectId);

        String prompt = "Suspect name: " + suspect.getName()
                + "\nSuspect age: " + suspect.getAge()
                + "\nPlayer question: " + dto.getQuestionText();

        String answer = openAiService.generateAnswer(prompt);
        if (answer == null || answer.isBlank()) {
            answer = "I do not have anything else to say right now.";
        }

        String audioFileName = elevenLabsService.generateVoice(answer);
        return new VoiceAnswerOut(answer, audioFileName);
    }

    public Suspect checkSuspect(Integer id) {
        Suspect suspect = suspectRepository.findSuspectById(id);
        if (suspect == null) throw new ApiException("Suspect not found");
        return suspect;
    }


}
