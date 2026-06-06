package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.IN.WitnessIn;
import com.example.redthreadgame.DTO.OUT.VoiceAnswerOut;
import com.example.redthreadgame.DTO.OUT.WitnessOut;
import com.example.redthreadgame.Model.Case;
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
    private final CaseService caseService;
    private final OpenAiService openAiService;
    private final ElevenLabsService elevenLabsService;

    public List<WitnessOut> getAllWitnesses() {
        List<WitnessOut> witnesses = new ArrayList<>();
        for (Witness w : witnessRepository.findAll()) {
            witnesses.add(modelMapper.map(w, WitnessOut.class));
        }
        return witnesses;
    }
    public void addWitness(Integer caseId, WitnessIn dto){
        Case c = caseService.checkCase(caseId);
        Witness witness= modelMapper.map(dto, Witness.class);
        witness.setWitnessCase(c);

        witnessRepository.save(witness);
    }
    public void updateWitness(Integer id, WitnessIn dto) {
        Witness old = checkWitness(id);
        old.setName(dto.getName());
        old.setStatement(dto.getStatement());
        old.setReliabilityScore(dto.getReliabilityScore());
        old.setGender(dto.getGender());
        old.setVoiceTone(dto.getVoiceTone());

        witnessRepository.save(old);
    }
   public void deleteWitness(Integer id){
        witnessRepository.delete(checkWitness(id));
   }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    public List<WitnessOut> getWitnessesDetails(Integer caseId) {
        caseService.checkCase(caseId);
        List<WitnessOut> witnesses = new ArrayList<>();
        for (Witness w : witnessRepository.findWitnessesByWitnessCaseId(caseId)) {
            witnesses.add(modelMapper.map(w, WitnessOut.class));

        }
        return witnesses;
    }

    //endpoint by mohammed
    public VoiceAnswerOut askWitness(Integer witnessId, QuestionIn dto) {
        Witness witness = checkWitness(witnessId);

        String prompt = "Witness name: " + witness.getName()
                + "\nWitness statement: " + witness.getStatement()
                + "\nWitness gender: " + witness.getGender()
                + "\nWitness voice tone: " + witness.getVoiceTone()
                + "\nRules: Answer in English only. Match the witness voice tone naturally. Do not include stage directions, brackets, emotion labels, or sound effects."
                + "\nPlayer question: " + dto.getQuestionText();

        String answer = openAiService.generateAnswer(prompt);
        if (answer == null || answer.isBlank()) {
            answer = witness.getStatement();
        }

        String audioFileName = elevenLabsService.generateVoice(answer, witness.getGender(), witness.getVoiceTone());
        return new VoiceAnswerOut(answer, audioFileName);
    }

    //helper method
    private Witness checkWitness(Integer id) {
        Witness witness = witnessRepository.findWitnessById(id);
        if (witness == null)
            throw new ApiException("Witness not found");
        return witness;
    }
}
