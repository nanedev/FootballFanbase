package com.malikbisic.sportapp.model.api;

/**
 * Created by Nane on 29.7.2017.
 */

public class CountryModel {
    private String countryName;
    private String countryImage;

    public CountryModel() {
    }

    public CountryModel(String countryName, String countryImage) {
        this.countryName = countryName;
        this.countryImage = countryImage;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryImage() {
        return countryImage;
    }

    public void setCountryImage(String countryImage) {
        this.countryImage = countryImage;
    }
}
