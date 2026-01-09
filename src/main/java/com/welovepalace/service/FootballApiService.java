package com.welovepalace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welovepalace.dto.MatchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FootballApiService {

    @Value("${football.api.key}")
    private String apiKey;

    @Value("${football.api.base-url:https://api.football-data.org/v4}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // Hämtar matcher och skapar prediktioner
    public List<MatchDto> getPremierLeagueMatches() throws Exception {
        String url = baseUrl + "/competitions/PL/matches";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode matches = root.get("matches");

        List<MatchDto> result = new ArrayList<>();

        for (JsonNode match : matches) {
            String status = match.get("status").asText();

            if (status.equals("FINISHED") || status.equals("SCHEDULED") || status.equals("TIMED")) {
                result.add(new MatchDto(
                        match.get("homeTeam").get("name").asText(),
                        match.get("awayTeam").get("name").asText(),
                        OffsetDateTime.parse(match.get("utcDate").asText()),
                        status,
                        match.get("score").get("fullTime").get("home").isNull() ? null :
                                match.get("score").get("fullTime").get("home").asInt(),
                        match.get("score").get("fullTime").get("away").isNull() ? null :
                                match.get("score").get("fullTime").get("away").asInt()
                ));
            }
        }

        // **Prediktioner baserat på tidigare resultat**
        predictScores(result);

        return result;
    }

    // Skapar score predictions
    public void predictScores(List<MatchDto> matches) {
        Map<String, List<Integer>> homeGoals = new HashMap<>();
        Map<String, List<Integer>> awayGoals = new HashMap<>();

        // Samla historik från FINISHED matcher
        for (MatchDto match : matches) {
            if ("FINISHED".equals(match.getStatus())) {
                homeGoals.computeIfAbsent(match.getHomeTeam(), k -> new ArrayList<>())
                        .add(match.getHomeScore());
                awayGoals.computeIfAbsent(match.getAwayTeam(), k -> new ArrayList<>())
                        .add(match.getAwayScore());
            }
        }

        // Prediktioner för kommande matcher
        for (MatchDto match : matches) {
            if (!"FINISHED".equals(match.getStatus())) {
                List<Integer> homeScores = homeGoals.getOrDefault(match.getHomeTeam(), List.of(1));
                List<Integer> awayScores = awayGoals.getOrDefault(match.getAwayTeam(), List.of(1));

                int predictedHome = (int) homeScores.stream().mapToInt(Integer::intValue).average().orElse(1);
                int predictedAway = (int) awayScores.stream().mapToInt(Integer::intValue).average().orElse(1);

                match.setPredictedHomeScore(predictedHome);
                match.setPredictedAwayScore(predictedAway);
            }
        }
    }
}
