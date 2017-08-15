package com.malikbisic.sportapp;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueModel {

    String name;
    String current_season_id;
    String country_name;

    public LeagueModel() {
    }

    public LeagueModel(String name, String current_season_id, String country_name) {
        this.name = name;
        this.current_season_id = current_season_id;
        this.country_name = country_name;
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
