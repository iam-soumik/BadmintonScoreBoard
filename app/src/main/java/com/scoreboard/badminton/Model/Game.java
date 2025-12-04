package com.scoreboard.badminton.Model;

import java.util.List;

/**
 * Created by HOME on 12/29/2018.
 */

public class Game {

    private int Game_number;
    private List<List<Integer>> pointsList;
    private List<Team> teamList;
    private int servingTeam;
/*
    Game(){
        pointsList = new ArrayList<List<Integer>>();
    }*/
    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public int getServingTeam() {
        return servingTeam;
    }

    public void setServingTeam(int servingTeam) {
        this.servingTeam = servingTeam;
    }

    public int getGame_number() {
        return Game_number;
    }

    public void setGame_number(int game_number) {
        Game_number = game_number;
    }

    public List<List<Integer>> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<List<Integer>> pointsList) {
        this.pointsList = pointsList;
    }
}
