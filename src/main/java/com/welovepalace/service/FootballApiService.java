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
import java.util.*;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Service
public class FootballApiService {

    @Value("${football.api.key}")
    private String apiKey;

    @Value("${football.api.base-url:https://api.football-data.org/v4}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();


    public List<MatchDto> getPremierLeagueMatches() throws Exception {
        String url = baseUrl + "/competitions/PL/matches";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode matches = root.get("matches");

        List<MatchDto> upcoming = new ArrayList<>();
        List<MatchDto> finished = new ArrayList<>();

        for (JsonNode match : matches) {
            String status = match.get("status").asText();

            if (status.equals("FINISHED") || status.equals("SCHEDULED") || status.equals("TIMED")) {

                MatchDto dto = new MatchDto(
                        match.get("homeTeam").get("name").asText(),
                        match.get("awayTeam").get("name").asText(),
                        OffsetDateTime.parse(match.get("utcDate").asText()),
                        status,
                        match.get("score").get("fullTime").get("home").isNull() ? null :
                                match.get("score").get("fullTime").get("home").asInt(),
                        match.get("score").get("fullTime").get("away").isNull() ? null :
                                match.get("score").get("fullTime").get("away").asInt()
                );

                if ("FINISHED".equals(status)) {
                    finished.add(dto);
                } else {
                    upcoming.add(dto);
                }
            }
        }

        // ✅ Upcoming: tidigast match först
        upcoming.sort((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()));

        // ✅ Finished: senaste match först
        finished.sort((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()));

        // Slå ihop
        List<MatchDto> result = new ArrayList<>();
        result.addAll(upcoming);
        result.addAll(finished);

        return result;
    }


    // Skapar score predictions
    // Skapar score predictions med realistisk variation
    public void predictScores(List<MatchDto> matches) {
        Map<String, List<Integer>> homeGoals = new HashMap<>();
        Map<String, List<Integer>> awayGoals = new HashMap<>();
        Random random = new Random();

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

                // Beräkna medelvärde
                double avgHome = homeScores.stream().mapToInt(Integer::intValue).average().orElse(1);
                double avgAway = awayScores.stream().mapToInt(Integer::intValue).average().orElse(1);

                // Lägg till slumpmässig variation ±1-2 mål
                int predictedHome = (int) Math.max(0, Math.round(avgHome + random.nextInt(3) - 1));
                int predictedAway = (int) Math.max(0, Math.round(avgAway + random.nextInt(3) - 1));

                match.setPredictedHomeScore(predictedHome);
                match.setPredictedAwayScore(predictedAway);
            }
        }
    }

    public void predictScoreForMatch(MatchDto match, List<MatchDto> allMatches) {
        String home = match.getHomeTeam();
        String away = match.getAwayTeam();

        // Samla historik
        List<MatchDto> finished = allMatches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .collect(Collectors.toList());

        // Hitta senaste 5 matcher för varje lag
        List<MatchDto> recentHome = finished.stream()
                .filter(m -> m.getHomeTeam().equals(home) || m.getAwayTeam().equals(home))
                .sorted((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()))
                .limit(5)
                .toList();

        List<MatchDto> recentAway = finished.stream()
                .filter(m -> m.getHomeTeam().equals(away) || m.getAwayTeam().equals(away))
                .sorted((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()))
                .limit(5)
                .toList();

        // Beräkna snittmål
        double avgHomeFor = recentHome.stream()
                .mapToInt(m -> m.getHomeTeam().equals(home) ? m.getHomeScore() : m.getAwayScore())
                .average().orElse(1.0);

        double avgAwayFor = recentAway.stream()
                .mapToInt(m -> m.getAwayTeam().equals(away) ? m.getAwayScore() : m.getHomeScore())
                .average().orElse(1.0);

        // Neutral defensiv trend: motståndares snitt insläppta mål
        double avgHomeAgainst = recentHome.stream()
                .mapToInt(m -> m.getHomeTeam().equals(home) ? m.getAwayScore() : m.getHomeScore())
                .average().orElse(1.0);

        double avgAwayAgainst = recentAway.stream()
                .mapToInt(m -> m.getAwayTeam().equals(away) ? m.getHomeScore() : m.getAwayScore())
                .average().orElse(1.0);

        // Baspoäng med hemmaplansfördel
        double predictedHome = (avgHomeFor + avgAwayAgainst) / 2 + 0.25;
        double predictedAway = (avgAwayFor + avgHomeAgainst) / 2;

        // Lägg till realistisk variation ±1 mål
        Random rand = new Random();
        int finalHome = Math.max(0, (int) Math.round(predictedHome + rand.nextInt(3) - 1));
        int finalAway = Math.max(0, (int) Math.round(predictedAway + rand.nextInt(3) - 1));

        match.setPredictedHomeScore(finalHome);
        match.setPredictedAwayScore(finalAway);
    }
}
