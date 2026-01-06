package com.welovepalace.controller;

import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller; // Ändrat från RestController
import org.springframework.ui.Model; // Viktigt för att skicka data till HTML

import java.util.List;

@Controller
public class MatchController {

    private final FootballApiService footballApiService;

    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/") // Nu visas sidan på http://localhost:8080/
    public String getMatches(Model model) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        
        // Detta gör listan "matches" tillgänglig i din HTML
        model.addAttribute("matches", matches); 
        
        return "index"; // Detta letar efter index.html i mappen src/main/resources/templates
    }
}
