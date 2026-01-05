package com.welovepalace.controller;

import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MatchController {

    private final FootballApiService footballApiService;

    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/matches")
    public List<MatchDto> getMatches() throws Exception {
        return footballApiService.getPremierLeagueMatches();
    }
}
