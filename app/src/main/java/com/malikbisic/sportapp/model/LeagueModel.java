package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueModel {

    String name;
    String current_season_id;
    String country_name;
    int league_id;

    public LeagueModel() {
    }

    public LeagueModel(String name, String current_season_id, String country_name, int league_id) {
        this.name = name;
        this.current_season_id = current_season_id;
        this.country_name = country_name;
        this.league_id = league_id;
    }

    public int getLeague_id() {
        return league_id;
    }

    public void setLeague_id(int league_id) {
        this.league_id = league_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrent_season_id() {
        return current_season_id;
    }

    public void setCurrent_season_id(String current_season_id) {
        this.current_season_id = current_season_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
}
