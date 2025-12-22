package com.welovepalace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class FootballApiService {

    @Value("${football.api.key}")
    private String apiKey;

    public void callApi() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);


    }
}
