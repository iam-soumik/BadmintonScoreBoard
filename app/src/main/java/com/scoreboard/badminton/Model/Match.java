package com.scoreboard.badminton.Model;

import java.util.List;

/**
 * Created by HOME on 12/29/2018.
 */

public class Match {
    private String matchID;
    private String[][] points;
    private List<Game> gameList;
    private List<Team> teamList;
    private Long matchDuration;
    private List<String> gameWinnerTeam;
    private String winnerTeam;
    private String allPoints;
    private String team1;
    private String team2;

    public String getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(String winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public String getMatchID() {
        return matchID;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public List<String> getGameWinnerTeam() {
        return gameWinnerTeam;
    }

    public void setGameWinnerTeam(List<String> gameWinnerTeam) {
        this.gameWinnerTeam = gameWinnerTeam;
    }

    public String getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(String allPoints) {
        this.allPoints = allPoints;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String[][] getPoints() {
        return points;
    }

    public void setPoints(String[][] points) {
        this.points = points;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public Long getMatchDuration() {
        return matchDuration;
    }

    public void setMatchDuration(Long matchDuration) {
        this.matchDuration = matchDuration;
    }
}
