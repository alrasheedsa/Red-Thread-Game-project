package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElevenLabsService {

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    @Value("${elevenlabs.api.url}")
    private String apiUrl;

    @Value("${elevenlabs.voice.id}")
    private String voiceId;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateVoice(String text) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new ApiException("ElevenLabs API key is missing");
            }
            if (voiceId == null || voiceId.isBlank()) {
                throw new ApiException("ElevenLabs voice id is missing");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("xi-api-key", apiKey);

            Map<String, Object> body = Map.of(
                    "text", text,
                    "model_id", "eleven_multilingual_v2"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            byte[] audio = restTemplate.postForObject(apiUrl + "/" + voiceId, request, byte[].class);

            if (audio == null || audio.length == 0) {
                throw new ApiException("Failed to generate voice");
            }

            Path uploadPath = Path.of("uploads");
            Files.createDirectories(uploadPath);

            String fileName = "voice_" + System.currentTimeMillis() + ".mp3";
            Files.write(uploadPath.resolve(fileName), audio);

            return fileName;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Failed to generate voice");
        }
    }
}
