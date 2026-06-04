package com.example.redthreadgame.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    //ترسل HTTP Requests
    private final RestTemplate restTemplate = new RestTemplate();

    public String whatsAppText(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Write a short Arabic WhatsApp notification. Output only the message. No questions. No question marks."));
        messages.add(Map.of("role", "user", "content", prompt));
        return chat(messages);
    }

    public String generateAnswer(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a mystery game character. Answer naturally, briefly, and stay consistent with the provided character information."));
        messages.add(Map.of("role", "user", "content", prompt));
        return chat(messages);
    }

    private String chat(List<Map<String, String>> messages) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                return "";
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); //API Key يرسل مصادقه

            Map<String, Object> body = Map.of("model", "gpt-4o-mini", "messages", messages);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            Map<?, ?> response = restTemplate.postForObject(apiUrl, request, Map.class);//يرسل POST Request ويرجع الرد

            if (response == null) {
                return "";
            }

            List<?> choices = (List<?>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "";
            }
            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");
            if (message == null || message.get("content") == null) {
                return "";
            }
            return message.get("content").toString().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
