package com.welovepalace.dto;

public class PredictionResultDto {
    private int homeScore;
    private int awayScore;
    private String explanation;

    public PredictionResultDto() {
    }

    public PredictionResultDto(int homeScore, int awayScore, String explanation) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.explanation = explanation;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}