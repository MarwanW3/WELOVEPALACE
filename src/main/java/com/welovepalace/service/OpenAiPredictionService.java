package com.welovepalace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welovepalace.dto.MatchDto;
import com.welovepalace.dto.PredictionResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiPredictionService {

    @Value("${OPENAI_API_KEY:${openai.api.key}}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PredictionResultDto predictScore(MatchDto targetMatch, List<MatchDto> allMatches) throws Exception {
        List<MatchDto> finished = allMatches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .sorted(Comparator.comparing(MatchDto::getUtcDate).reversed())
                .toList();

        String homeTeam = targetMatch.getHomeTeam();
        String awayTeam = targetMatch.getAwayTeam();

        List<MatchDto> recentHome = finished.stream()
                .filter(m -> m.getHomeTeam().equals(homeTeam) || m.getAwayTeam().equals(homeTeam))
                .limit(5)
                .toList();

        List<MatchDto> recentAway = finished.stream()
                .filter(m -> m.getHomeTeam().equals(awayTeam) || m.getAwayTeam().equals(awayTeam))
                .limit(5)
                .toList();

        String prompt = buildPrompt(targetMatch, recentHome, recentAway);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "You predict football scores. Return ONLY valid JSON."
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1-mini");
        body.put("messages", List.of(systemMessage, userMessage));
        body.put("temperature", 0.4);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        String responseBody = response.getBody();
        JsonNode root = objectMapper.readTree(responseBody);

        String content = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("Could not extract JSON from model response: " + content);
        }

        String jsonText = content.substring(start, end + 1);
        JsonNode json = objectMapper.readTree(jsonText);

        int homeScore = json.path("homeScore").asInt();
        int awayScore = json.path("awayScore").asInt();
        String explanation = json.path("explanation").asText();

        return new PredictionResultDto(homeScore, awayScore, explanation);
    }

    private String buildPrompt(MatchDto targetMatch, List<MatchDto> recentHome, List<MatchDto> recentAway) {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "Predict the football match score using only the provided data.\n" +
                        "Be realistic and conservative.\n\n" +
                        "Return ONLY valid JSON in exactly this format:\n" +
                        "{\n" +
                        "  \"homeScore\": 0,\n" +
                        "  \"awayScore\": 0,\n" +
                        "  \"explanation\": \"short explanation\"\n" +
                        "}\n\n" +
                        "Rules:\n" +
                        "- explanation must be at most 20 words\n" +
                        "- scores must be integers from 0 to 5\n" +
                        "- do not include markdown\n" +
                        "- do not include any text before or after the JSON\n"
        );

        sb.append("\nTarget match:\n");
        sb.append(targetMatch.getHomeTeam())
                .append(" vs ")
                .append(targetMatch.getAwayTeam())
                .append("\n");

        sb.append("\nRecent matches for home team:\n");
        for (MatchDto m : recentHome) {
            sb.append(formatMatch(m)).append("\n");
        }

        sb.append("\nRecent matches for away team:\n");
        for (MatchDto m : recentAway) {
            sb.append(formatMatch(m)).append("\n");
        }

        return sb.toString();
    }

    private String formatMatch(MatchDto m) {
        return String.format(
                "%s vs %s => %d-%d",
                m.getHomeTeam(),
                m.getAwayTeam(),
                m.getHomeScore(),
                m.getAwayScore()
        );
    }
}