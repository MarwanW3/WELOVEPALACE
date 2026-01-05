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
<<<<<<< Updated upstream

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
=======
>>>>>>> Stashed changes

@Service
public class FootballApiService {

    @Value("${football.api.key}")
    private String apiKey;

<<<<<<< Updated upstream
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<MatchDto> getMatches() throws Exception {
=======
    @Value("${football.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getPremierLeagueMatches() {

        String url = baseUrl + "/matches";
>>>>>>> Stashed changes

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
<<<<<<< Updated upstream
                restTemplate.exchange(
                        "https://api.football-data.org/v4/competitions/PL/matches",
                        HttpMethod.GET,
                        entity,
                        String.class
                );


        JsonNode root = mapper.readTree(response.getBody());
        JsonNode matches = root.get("matches");

        List<MatchDto> result = new ArrayList<>();

        for (JsonNode match : matches) {
            String status = match.get("status").asText();

            if (status.equals("FINISHED") || status.equals("SCHEDULED")) {
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
=======
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
>>>>>>> Stashed changes
    }
}


