package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.IN.WitnessIn;
import com.example.redthreadgame.DTO.OUT.VoiceAnswerOut;
import com.example.redthreadgame.DTO.OUT.WitnessOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.QuestionTargetType;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Question;
import com.example.redthreadgame.Model.Witness;
import com.example.redthreadgame.Repository.GameSessionRepository;
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
    private final GameSessionRepository gameSessionRepository;

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


    public String confrontWitnesses(Integer witnessId1, Integer witnessId2, Integer gameSessionId) {
        GameSession gameSession = checkGameSession(gameSessionId);
        Witness witness1 = checkWitness(witnessId1);
        Witness witness2 = checkWitness(witnessId2);

        if (!witness1.getWitnessCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Witness 1 does not belong to this case");
        if (!witness2.getWitnessCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Witness 2 does not belong to this case");

        String prompt = """
            You are a detective analyst.
            
            Witness 1: %s
            Statement: %s
            
            Witness 2: %s
            Statement: %s
            
            Analyze both statements and identify:
            1. Where they agree
            2. Where they contradict each other
            3. Which witness seems more credible and why
            
            Respond in this exact JSON format:
            {
              "agree": "what they agree on",
              "contradict": "where they contradict",
              "moreCredible": "which witness is more credible and why"
            }
            Return ONLY the JSON, no extra text.
            """.formatted(
                witness1.getName(), witness1.getStatement(),
                witness2.getName(), witness2.getStatement()
        );

        String result = openAiService.generateAnswer(prompt);
        return result.trim().replace("```json", "").replace("```", "").trim();
    }

    public String retractWitnessStatement(Integer witnessId, Integer gameSessionId) {
        // تحقق من الجلسة
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null) throw new ApiException("Game session not found");
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        // تحقق من الشاهد
        Witness witness = checkWitness(witnessId);
        if (!witness.getWitnessCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Witness does not belong to this case");

        String prompt = """
            You are roleplaying as a witness in a detective mystery game who is now retracting their statement.
            
            Case scenario: %s
            
            Witness name: %s
            Original statement: %s
            Witness voice tone: %s
            Witness reliability score: %s out of 100
            
            The witness is now scared, pressured, or hiding something.
            They retract part of their original statement and give a new contradicting version.
            
            Rules:
            - Do NOT reveal who is guilty.
            - Make the new statement contradict the original in a subtle but suspicious way.
            - Match the witness personality and voice tone.
            - Make it dramatic and leave the players confused.
            - Answer in English only.
            
            Respond in this exact JSON format:
            {
              "originalStatement": "the original statement",
              "newStatement": "the new contradicting statement",
              "reason": "why the witness is retracting - scared, paid off, hiding something, etc",
              "suspicionLevel": "HIGH or MEDIUM or LOW"
            }
            Return ONLY the JSON, no extra text.
            """.formatted(
                gameSession.getSessionCase().getScenario(),
                witness.getName(),
                witness.getStatement(),
                witness.getVoiceTone(),
                witness.getReliabilityScore()
        );

        String result = openAiService.generateAnswer(prompt);
        return result.trim().replace("```json", "").replace("```", "").trim();
    }

    public List<WitnessOut> getNotQuestionedWitnesses(Integer gameSessionId) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null) throw new ApiException("Game session not found");

        // كل شهود القضية
        List<Witness> allWitnesses = witnessRepository
                .findWitnessesByWitnessCaseId(gameSession.getSessionCase().getId());

        // ids الشهود اللي تم سؤالهم في هذه الجلسة
        List<Integer> questionedIds = new ArrayList<>();
        for (Question q : gameSession.getQuestions()) {
            if (q.getTargetType() == QuestionTargetType.WITNESS && q.getWitness() != null) {
                questionedIds.add(q.getWitness().getId());
            }
        }

        // حذف اللي تم سؤالهم
        List<WitnessOut> notQuestioned = new ArrayList<>();
        for (Witness w : allWitnesses) {
            if (!questionedIds.contains(w.getId())) {
                notQuestioned.add(modelMapper.map(w, WitnessOut.class));
            }
        }

        if (notQuestioned.isEmpty())
            throw new ApiException("All witnesses have been questioned");

        return notQuestioned;
    }

    //helper method
    private Witness checkWitness(Integer id) {
        Witness witness = witnessRepository.findWitnessById(id);
        if (witness == null)
            throw new ApiException("Witness not found");
        return witness;
    }

    private GameSession checkGameSession(Integer id) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if (gameSession == null) throw new ApiException("Game session not found");
        return gameSession;
    }
}
