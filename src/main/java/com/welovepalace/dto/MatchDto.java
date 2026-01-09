package com.welovepalace.dto;

import java.time.OffsetDateTime;

public class MatchDto {

    private String homeTeam;
    private String awayTeam;
    private OffsetDateTime utcDate;
    private String status;
    private Integer homeScore;
    private Integer awayScore;
    private Integer predictedHomeScore;
    private Integer predictedAwayScore;


    public Integer getPredictedHomeScore() { return predictedHomeScore; }
    public void setPredictedHomeScore(Integer predictedHomeScore) { this.predictedHomeScore = predictedHomeScore; }

    public Integer getPredictedAwayScore() { return predictedAwayScore; }
    public void setPredictedAwayScore(Integer predictedAwayScore) { this.predictedAwayScore = predictedAwayScore; }


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
