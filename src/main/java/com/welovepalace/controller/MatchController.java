package com.welovepalace.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MatchController {

    private static final String API_URL = "https://api.football-data.org/v4/matches";

    @Value("${football.api.key}")
    private String apiKey;

    @GetMapping("/matches")
    public String getMatches() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();
    }
}
