package com.welovepalace.model;
import java.util.List;

public class Matchresponse {
    private int count;
    private List<Match> matches;

    // getters & setters
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}

