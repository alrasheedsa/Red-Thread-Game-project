package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final CaseRepository caseRepository;
    private final WitnessRepository witnessRepository;
    private final SuspectRepository suspectRepository;
    private final EvidenceRepository evidenceRepository;
    private final CaseSolutionRepository caseSolutionRepository;
    private final AdminService adminService;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //generate case
    public void generateCase(Integer adminId, String password) {
        adminService.verifyAdmin(adminId, password);
        String prompt = """
                Generate a creative mystery case in English only. The case type can be murder, theft, kidnapping, fraud, or any other interesting crime that fits a detective game.
                Choose the case type naturally. Choose suspect ages and witness reliability scores naturally based on their role in the case.
                For every witness and suspect, choose gender and voiceTone.
                gender must be only: MALE or FEMALE.
                voiceTone must be only one of: CALM, NERVOUS, DEFENSIVE, SUSPICIOUS, SAD, CONFIDENT.
                Choose the tone based on the character role in the mystery.
                Use this exact JSON structure:
                        {
                          "title": "case title",
                          "scenario": "detailed case scenario 3-5 sentences",
                        "difficulty": "%s"
                
                        "witnesses": [
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"},
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"},
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"}
                       ],
                          "suspects": [
                            {"name": "suspect name", "age": choose_naturally, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"},
                            {"name": "suspect name", "age": choose_naturally, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"},
                            {"name": "suspect name", "age": choose_naturally, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"},
                            {"name": "child suspect", "age": choose_between_8_and_14, "gender": "MALE or FEMALE", "voiceTone": "CALM or NERVOUS or DEFENSIVE or SUSPICIOUS or SAD or CONFIDENT"}
                          ],
                          "evidences": [
                            {"title": "evidence title", "description": "evidence description"},
                            {"title": "evidence title", "description": "evidence description"}
                          ],
                          "caseSolution": {
                            "justification": "full explanation of who did it and why"
                          }
                        }
                        Return ONLY the JSON, no extra text.
                """;

        String response = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .build()
                .post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                          "model": "gpt-4o-mini",
                          "messages": [{"role": "user", "content": "%s"}],
                          "temperature": 0.8
                        }
                        """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"))).retrieve()
                .bodyToMono(String.class).block();

        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            content = content.replace("```json", "").replace("```", "").trim();
            JsonNode caseJson = objectMapper.readTree(content);

            Case newCase = new Case();
            newCase.setTitle(caseJson.path("title").asText());
            newCase.setScenario(caseJson.path("scenario").asText());
            newCase.setDifficulty(getNextDifficulty());
            newCase.setStatus("DRAFT");
            caseRepository.save(newCase);

            for (JsonNode w : caseJson.path("witnesses")) {
                Witness witness = new Witness();
                witness.setName(w.path("name").asText());
                witness.setStatement(w.path("statement").asText());
                witness.setReliabilityScore(w.path("reliabilityScore").asDouble());
                witness.setGender(normalizeGender(w.path("gender").asText()));
                witness.setVoiceTone(normalizeVoiceTone(w.path("voiceTone").asText(), "CALM"));
                witness.setWitnessCase(newCase);
                witnessRepository.save(witness);
            }

            for (JsonNode s : caseJson.path("suspects")) {
                Suspect suspect = new Suspect();
                suspect.setName(s.path("name").asText());
                suspect.setAge(s.path("age").asInt());
                suspect.setGender(normalizeGender(s.path("gender").asText()));
                suspect.setVoiceTone(normalizeVoiceTone(s.path("voiceTone").asText(), "DEFENSIVE"));
                suspect.setSuspectCase(newCase);
                suspectRepository.save(suspect);
            }

            for (JsonNode e : caseJson.path("evidences")) {
                Evidence evidence = new Evidence();
                evidence.setTitle(e.path("title").asText());
                evidence.setDescription(e.path("description").asText());
                evidence.setEvidenceCase(newCase);
                evidenceRepository.save(evidence);
            }
            CaseSolution solution = new CaseSolution();
            solution.setJustification(caseJson.path("caseSolution").path("justification").asText());
            solution.setSolutionCase(newCase);
            caseSolutionRepository.save(solution);

        } catch (Exception e) {
            throw new ApiException("Failed to parse AI response: " + e.getMessage());
        }
    }


//generate answer
    public String generateAnswer(String prompt) {
        String response = WebClient.builder().baseUrl("https://api.openai.com").build().post().uri("/v1/chat/completions").header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                          "model": "gpt-4o-mini",
                          "messages": [{"role": "user", "content": "%s"}],
                          "temperature": 0.7
                        }
                        """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"))).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new ApiException("Failed to generate answer: " + e.getMessage());
        }
    }

    //evaluation solution
    public String evaluateSolution(String playerReason, String accusedSuspectName, Integer accusedSuspectAge, String correctJustification) {
        String prompt = """
            You are a mystery game judge analyzing a detective team's performance.
            
            Correct solution justification:
            %s
            
            Player accused suspect:
            Name: %s
            Age: %s
            
            Player reason:
            %s
            
            The correct suspect is not stored in a separate database field.
            Extract the correct suspect from the correct solution justification.
            
            Analyze whether the player correctly identified the culprit and provided a reasonable explanation.
            
            Respond in this exact JSON format:
            {
              "isCorrect": true or false,
              "result": "You won! Great detective work!" or "You lost! Better luck next time!",
              "analysis": "2-3 sentences analyzing how well the team played",
              "focusOn": "1-2 specific areas to improve next time"
            }
            Return ONLY the JSON, no extra text.
            """.formatted(correctJustification, accusedSuspectName, accusedSuspectAge, playerReason);

        String response = WebClient.builder()
                .baseUrl("https://api.openai.com").build().post().uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("""
                    {
                      "model": "gpt-4o-mini",
                      "messages": [{"role": "user", "content": "%s"}],
                      "temperature": 0.0
                    }
                    """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n")))
                .retrieve().bodyToMono(String.class).block();

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText().trim()
                    .replace("```json", "").replace("```", "").trim();
        } catch (Exception e) {
            throw new ApiException("Failed to evaluate solution: " + e.getMessage());
        }
    }

    //calculate score
    public Integer calculateScore(Integer questionCount, Integer hintCount) {
        int baseScore = 100;
        int questionPenalty = questionCount * 5;
        int hintPenalty = hintCount * 10;
        int finalScore = baseScore - questionPenalty - hintPenalty;
        return Math.max(1, finalScore);
    }

    private String normalizeGender(String gender) {
        if ("FEMALE".equalsIgnoreCase(gender))
            return "FEMALE";
        return "MALE";
    }

    private String normalizeVoiceTone(String voiceTone, String defaultTone) {
        if (voiceTone == null)
            return defaultTone;

        String tone = voiceTone.toUpperCase();
        if (tone.equals("CALM") || tone.equals("NERVOUS") || tone.equals("DEFENSIVE") ||
                tone.equals("SUSPICIOUS") || tone.equals("SAD") || tone.equals("CONFIDENT"))
            return tone;

        return defaultTone;
    }


    private String getNextDifficulty() {
        Case lastCase = caseRepository.findFirstByOrderByIdDesc();

        if (lastCase == null)
            return "EASY";

        return switch (lastCase.getDifficulty()) {
            case "EASY"   -> "MEDIUM";
            case "MEDIUM" -> "HARD";
            default       -> "EASY";
        };
    }
    }
