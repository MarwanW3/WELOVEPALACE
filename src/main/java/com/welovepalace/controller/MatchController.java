package com.welovepalace.controller;

import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class MatchController {

    private final FootballApiService footballApiService;

    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/")
    public String getMatches(Model model) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        List<MatchDto> upcomingMatches = matches.stream()
                .filter(m -> !"FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()))
                .toList();

        List<MatchDto> finishedMatches = matches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()))
                .toList();

        OffsetDateTime now = OffsetDateTime.now();
        LocalDate todayDate = LocalDate.now();
        int weekNumber = todayDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        OffsetDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        OffsetDateTime lastWeek = now.minusDays(7);

        model.addAttribute("upcomingMatches", upcomingMatches);
        model.addAttribute("finishedMatches", finishedMatches);
        model.addAttribute("currentWeek", weekNumber);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("now", now);

        return "index";
    }

    // Full-page reload prediktion (behålls om du vill)
    @GetMapping("/predictScore")
    public String predictScore(Integer matchIndex, Model model) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        if (matchIndex != null && matchIndex >= 0 && matchIndex < matches.size()) {
            MatchDto match = matches.get(matchIndex);
            footballApiService.predictScores(List.of(match));
        }

        // Lägg tillbaka samma attribut som i getMatches()
        List<MatchDto> upcomingMatches = matches.stream()
                .filter(m -> !"FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()))
                .toList();

        List<MatchDto> finishedMatches = matches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()))
                .toList();

        OffsetDateTime now = OffsetDateTime.now();
        LocalDate todayDate = LocalDate.now();
        int weekNumber = todayDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        OffsetDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        OffsetDateTime lastWeek = now.minusDays(7);

        model.addAttribute("upcomingMatches", upcomingMatches);
        model.addAttribute("finishedMatches", finishedMatches);
        model.addAttribute("currentWeek", weekNumber);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("now", now);

        return "index";
    }

    // AJAX-prediktion, returnerar poängen utan att ladda om sidan
    @GetMapping("/predictScoreAjax")
    @ResponseBody
    public Map<String, Integer> predictScoreAjax(Integer matchIndex) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        if (matchIndex != null && matchIndex >= 0 && matchIndex < matches.size()) {
            MatchDto match = matches.get(matchIndex);
            footballApiService.predictScoreForMatch(match, matches);

            return Map.of(
                    "homeScore", match.getPredictedHomeScore(),
                    "awayScore", match.getPredictedAwayScore()
            );
        }

        return Map.of();
    }
}
