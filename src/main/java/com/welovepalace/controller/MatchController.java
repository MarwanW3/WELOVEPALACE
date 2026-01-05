package com.welovepalace.controller;

<<<<<<< Updated upstream
import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
=======
import com.welovepalace.service.FootballApiService;
>>>>>>> Stashed changes
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MatchController {

    private final FootballApiService footballApiService;

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/matches")
<<<<<<< Updated upstream
    public List<MatchDto> getMatches() throws Exception {
        return footballApiService.getMatches();
=======
    public String getMatches() {
        return footballApiService.getPremierLeagueMatches();
>>>>>>> Stashed changes
    }
}
