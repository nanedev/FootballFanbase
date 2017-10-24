package com.malikbisic.sportapp.model;

/**
 * Created by Nane on 20.10.2017.
 */

public class TableModel {
    private int position;
    private int teamId;
    private String teamName;
    private int played;
    private int wins;
    private int draws;
    private int loses;
    private int goalScored;
    private int goalConcided;
    private String goalDif;
    private int points;
    String clubLogo;
    String countryId;


    public TableModel() {
    }

    public TableModel(int position, int teamId, String teamName, int played, int wins, int draws, int loses, int goalScored, int goalConcided, String goalDif, int points,String clubLogo,String countryId) {
        this.position = position;
        this.teamId = teamId;
        this.teamName = teamName;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
        this.loses = loses;
        this.goalScored = goalScored;
        this.goalConcided = goalConcided;
        this.goalDif = goalDif;
        this.points = points;
        this.clubLogo = clubLogo;
        this.countryId = countryId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getGoalScored() {
        return goalScored;
    }

    public void setGoalScored(int goalScored) {
        this.goalScored = goalScored;
    }

    public int getGoalConcided() {
        return goalConcided;
    }

    public void setGoalConcided(int goalConcided) {
        this.goalConcided = goalConcided;
    }

    public String getGoalDif() {
        return goalDif;
    }

    public void setGoalDif(String goalDif) {
        this.goalDif = goalDif;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
