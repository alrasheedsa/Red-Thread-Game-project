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

    @Value("${elevenlabs.voice.male}")
    private String maleVoiceId;

    @Value("${elevenlabs.voice.female}")
    private String femaleVoiceId;

    private final RestTemplate restTemplate = new RestTemplate();

    // Fallback voice generation for old calls that do not provide gender or tone.
    public String generateVoice(String text) {
        return generateVoice(text, "MALE", "CALM");
    }

    // Generates an audio file from text using the selected gender voice and tone settings.
    public String generateVoice(String text, String gender, String voiceTone) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new ApiException("ElevenLabs API key is missing");
            }
            String voiceId = resolveVoiceId(gender);
            if (voiceId == null || voiceId.isBlank()) {
                throw new ApiException("ElevenLabs voice id is missing");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("xi-api-key", apiKey);

            Map<String, Object> voiceSettings = Map.of(
                    "stability", getStability(voiceTone),
                    "similarity_boost", 0.85,
                    "style", getStyleExaggeration(voiceTone),
                    "use_speaker_boost", true
            );

            Map<String, Object> body = Map.of(
                    "text", text,
                    "model_id", "eleven_multilingual_v2",
                    "voice_settings", voiceSettings
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

    // Chooses the ElevenLabs voice id based on the character gender.
    private String resolveVoiceId(String gender) {
        if ("FEMALE".equalsIgnoreCase(gender)) {
            return femaleVoiceId;
        }
        return maleVoiceId;
    }

    // Controls how stable or expressive the voice should be for each tone.
    private Double getStability(String voiceTone) {
        if ("NERVOUS".equalsIgnoreCase(voiceTone)) return 0.30;
        if ("DEFENSIVE".equalsIgnoreCase(voiceTone)) return 0.40;
        if ("SUSPICIOUS".equalsIgnoreCase(voiceTone)) return 0.35;
        if ("SAD".equalsIgnoreCase(voiceTone)) return 0.45;
        if ("CONFIDENT".equalsIgnoreCase(voiceTone)) return 0.65;
        return 0.55;
    }

    // Controls how strongly ElevenLabs should apply style and emotion to the voice.
    private Double getStyleExaggeration(String voiceTone) {
        if ("NERVOUS".equalsIgnoreCase(voiceTone)) return 0.65;
        if ("DEFENSIVE".equalsIgnoreCase(voiceTone)) return 0.55;
        if ("SUSPICIOUS".equalsIgnoreCase(voiceTone)) return 0.60;
        if ("SAD".equalsIgnoreCase(voiceTone)) return 0.45;
        if ("CONFIDENT".equalsIgnoreCase(voiceTone)) return 0.35;
        return 0.40;
    }
}
