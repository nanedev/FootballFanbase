package com.malikbisic.sportapp;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueModel {

    String name;
    String current_season_id;

    public LeagueModel() {
    }

    public LeagueModel(String name, String current_season_id) {
        this.name = name;
        this.current_season_id = current_season_id;
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
}
