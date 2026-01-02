package com.welovepalace.dto;

import java.time.OffsetDateTime;

public class MatchDto {

    private String homeTeam;
    private String awayTeam;
    private OffsetDateTime utcDate;
    private String status;
    private Integer homeScore;
    private Integer awayScore;

    public MatchDto(String homeTeam, String awayTeam, OffsetDateTime utcDate, String status, Integer homeScore, Integer awayScore) {

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.utcDate = utcDate;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public OffsetDateTime getUtcDate() {
        return utcDate;
    }

    public String getStatus() {
        return status;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }
}
