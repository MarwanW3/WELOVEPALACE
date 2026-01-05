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
import java.util.List;

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

        return result;
    }
}
