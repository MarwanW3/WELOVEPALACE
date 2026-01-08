package com.welovepalace.controller;

import com.welovepalace.dto.MatchDto;
import com.welovepalace.service.FootballApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class MatchController {

    private final FootballApiService footballApiService;

    public MatchController(FootballApiService footballApiService) {
        this.footballApiService = footballApiService;
    }

    @GetMapping("/")
    public String getMatches(Model model) throws Exception {


        List<MatchDto> matches = footballApiService.getPremierLeagueMatches();
        if (matches == null) {
            matches = new ArrayList<>();
        }


        matches.sort((m1, m2) -> {
            if (m1.getUtcDate() == null && m2.getUtcDate() == null) return 0;
            if (m1.getUtcDate() == null) return 1;  // null längst ner
            if (m2.getUtcDate() == null) return -1;
            return m2.getUtcDate().compareTo(m1.getUtcDate());
        });


        OffsetDateTime now = OffsetDateTime.now();
        LocalDate todayDate = LocalDate.now();


        int weekNumber = todayDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());


        OffsetDateTime startOfWeek = now.with(DayOfWeek.MONDAY)
                .withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endOfWeek = startOfWeek.plusDays(6)
                .withHour(23).withMinute(59).withSecond(59);


        OffsetDateTime lastWeek = now.minusDays(7);


        model.addAttribute("matches", matches);
        model.addAttribute("currentWeek", weekNumber);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("now", now);

        return "index";
    }
}
