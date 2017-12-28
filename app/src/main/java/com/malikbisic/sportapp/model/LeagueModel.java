package com.malikbisic.sportapp.model;

import android.support.annotation.NonNull;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueModel {

    String name;
    String current_season_id;
    String country_name;
    int league_id;

    int fixtureId;
    String date;

    public LeagueModel() {
    }

    public LeagueModel(String name, String current_season_id, String country_name, int league_id, int fixtureId,String date) {
        this.name = name;
        this.current_season_id = current_season_id;
        this.country_name = country_name;
        this.league_id = league_id;
        this.fixtureId = fixtureId;
        this.date = date;
    }

    public LeagueModel(String name, String current_season_id, String country_name, int league_id) {
        this.name = name;
        this.current_season_id = current_season_id;
        this.country_name = country_name;
      this.league_id = league_id;
        this.fixtureId = fixtureId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFixtureId() {
        return fixtureId;
    }

    public void setFixtureId(int fixtureId) {
        this.fixtureId = fixtureId;
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LeagueModel other = (LeagueModel) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (country_name == null) {
            if (other.country_name != null)
                return false;
        } else if (!country_name.equals(other.country_name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((country_name == null) ? 0 : country_name.hashCode());
        return result;
    }
}
