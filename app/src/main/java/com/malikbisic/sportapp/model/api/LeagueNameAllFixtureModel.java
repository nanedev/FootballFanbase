package com.malikbisic.sportapp.model.api;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by korisnik on 27/12/2017.
 */

public class LeagueNameAllFixtureModel extends ExpandableGroup {

    String countryName;

    public LeagueNameAllFixtureModel(String title, List items, String countryName) {
        super(title, items);
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((countryName == null) ? 0 : countryName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LeagueNameAllFixtureModel other = (LeagueNameAllFixtureModel) obj;
        if (getTitle() == null) {
            if (other.getTitle() != null)
                return false;
        } else if (!getTitle().equals(other.getTitle()))
            return false;
        if (countryName == null) {
            if (other.countryName != null)
                return false;
        } else if (!countryName.equals(other.countryName))
            return false;
        return true;
    }
}
