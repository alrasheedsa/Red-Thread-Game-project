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
    private final GameSessionRepository gameSessionRepository;


    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //generate case
    public void generateCase(Integer adminId, String password) {
        adminService.verifyAdmin(adminId, password);
        String prompt = """
                Generate a creative mystery case. The case type can be murder, theft, kidnapping, fraud, or any other interesting crime that fits a detective game.
                Choose the case type naturally. Choose suspect ages and witness reliability scores naturally based on their role in the case. Use this exact JSON structure:
                        {
                          "title": "case title",
                          "scenario": "detailed case scenario 3-5 sentences",
                          "difficulty": "EASY or MEDIUM or HARD",
                        "witnesses": [
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility},
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility},
                       {"name": "witness name", "statement": "witness statement", "reliabilityScore": score between 1 and 100 based on witness credibility}
                       ],
                          "suspects": [
                            {"name": "suspect name", "age": choose_naturally},
                            {"name": "suspect name", "age": choose_naturally},
                            {"name": "suspect name", "age": choose_naturally},
                            {"name": "child suspect", "age": choose_between_8_and_14}
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
            newCase.setDifficulty(caseJson.path("difficulty").asText());
            newCase.setStatus("DRAFT");
            caseRepository.save(newCase);

            for (JsonNode w : caseJson.path("witnesses")) {
                Witness witness = new Witness();
                witness.setName(w.path("name").asText());
                witness.setStatement(w.path("statement").asText());
                witness.setReliabilityScore(w.path("reliabilityScore").asDouble());
                witness.setWitnessCase(newCase);
                witnessRepository.save(witness);
            }

            for (JsonNode s : caseJson.path("suspects")) {
                Suspect suspect = new Suspect();
                suspect.setName(s.path("name").asText());
                suspect.setAge(s.path("age").asInt());
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
    public boolean evaluateSolution(String playerReason, String correctJustification) {
        String prompt = """
                You are a mystery game judge.
                
                Correct solution: %s
                
                Player's answer: %s
                
                Does the player's answer correctly identify the culprit and the motive?
                Reply with ONLY "true" or "false".
                """.formatted(correctJustification, playerReason);

        String response = WebClient.builder().baseUrl("https://api.openai.com").build().post().uri("/v1/chat/completions").header("Authorization", "Bearer " + openAiApiKey).header("Content-Type", "application/json")
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
            String result = root.path("choices").get(0).path("message").path("content").asText().trim().toLowerCase();
            return result.equals("true");
        } catch (Exception e) {
            throw new ApiException("Failed to evaluate solution: " + e.getMessage());
        }
    }

    }
