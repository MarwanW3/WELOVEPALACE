package com.welovepalace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FootballApiService {

    @Value("${football.api.key}")
    private String apiKey;

    @Value("${football.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getPremierLeagueMatches() {

        String url = baseUrl + "/matches";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
