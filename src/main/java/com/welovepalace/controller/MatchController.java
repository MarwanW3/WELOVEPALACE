package com.welovepalace.controller;

import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import com.welovepalace.dto.PredictionResultDto;
import com.welovepalace.service.OpenAiPredictionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Controller
public class MatchController {

    private final FootballApiService footballApiService;
    private final OpenAiPredictionService openAiPredictionService;

    public MatchController(FootballApiService footballApiService,
                           OpenAiPredictionService openAiPredictionService) {
        this.footballApiService = footballApiService;
        this.openAiPredictionService = openAiPredictionService;
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


        model.addAttribute("currentWeek", weekNumber);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("now", now);

        return "index";
    }

    // Full-page reload prediction
    @GetMapping("/predictScore")
    public String predictScore() {
        return "redirect:/";
    }

    /**
     * Predicts the score for a given match.
     *
     * @param matchIndex index of the match in the current list
     * @return predicted home score, away score and explanation
     */
    @GetMapping("/api/predict")
    @ResponseBody
    public Map<String, Object> predictScoreAjax(Integer matchIndex) {
        try {
            List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
            if (matches == null) matches = new ArrayList<>();

            List<MatchDto> upcomingMatches = matches.stream()
                    .filter(m -> !"FINISHED".equals(m.getStatus()))
                    .sorted((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()))
                    .toList();

            if (matchIndex != null && matchIndex >= 0 && matchIndex < upcomingMatches.size()) {
                MatchDto match = upcomingMatches.get(matchIndex);

                PredictionResultDto prediction = openAiPredictionService.predictScore(match, matches);

                return Map.of(
                        "homeScore", prediction.getHomeScore(),
                        "awayScore", prediction.getAwayScore(),
                        "explanation", prediction.getExplanation()
                );
            }

            return Map.of("error", "Invalid match index");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "error", e.getClass().getSimpleName(),
                    "message", String.valueOf(e.getMessage())
            );
        }
    }

    @GetMapping("upcoming_games")
    public String getUpcomingGames(Model model) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        List<MatchDto> upcomingMatches = matches.stream()
                .filter(m -> !"FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()))
                .toList();

        model.addAttribute("upcomingMatches", upcomingMatches);
        return "upcoming_games";
    }

    @GetMapping("finished_games")
    public String getFinishedGames(Model model) throws Exception {
        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        List<MatchDto> finishedMatches = matches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> b.getUtcDate().compareTo(a.getUtcDate()))
                .toList();

        model.addAttribute("finishedMatches", finishedMatches);
        return "finished_games";
    }

    @GetMapping("/api/matches/top5")
    @ResponseBody
    public List<Map<String, Object>> getTop5MatchesWithPredictions() throws Exception {

        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) matches = new ArrayList<>();

        List<MatchDto> upcomingMatches = matches.stream()
                .filter(m -> !"FINISHED".equals(m.getStatus()))
                .sorted((a, b) -> a.getUtcDate().compareTo(b.getUtcDate()))
                .limit(5)
                .toList();

        List<Map<String, Object>> resultList = new ArrayList<>();


        for (MatchDto match : upcomingMatches) {
            try {
                PredictionResultDto prediction =
                        openAiPredictionService.predictScore(match, matches);

                Map<String, Object> result = new HashMap<>();
                result.put("homeTeam", match.getHomeTeam());
                result.put("awayTeam", match.getAwayTeam());
                result.put("predictedHomeScore", prediction.getHomeScore());
                result.put("predictedAwayScore", prediction.getAwayScore());
                result.put("explanation", prediction.getExplanation());

                resultList.add(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }
}
