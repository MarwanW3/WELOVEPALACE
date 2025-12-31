package com.welovepalace.controller;

import com.welovepalace.service.FootballApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

    private final FootballApiService footballApiService;


    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/matches")
    public String getMatches() {
        return footballApiService.getPremierLeagueMatches();
    }
}
