package com.malikbisic.sportapp.model.firebase;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by korisnik on 03/08/2017.
 */

public class UsersModel {

    String country;
    String date;
    String favoriteClub;
    String favoriteClubLogo;
    String flag;
    String gender;
    String name;
    String profileImage;
    String surname;
    String userID;
    String username;
    boolean premium;
    String favoritePostClub;

    public UsersModel() {
    }

    public UsersModel(String country, String date, String favoriteClub, String favoriteClubLogo, String flag, String gender, String name, String profileImage, String surname, String userID, String username, boolean premium, String favoritePostClub) {
        this.country = country;
        this.date = date;
        this.favoriteClub = favoriteClub;
        this.favoriteClubLogo = favoriteClubLogo;
        this.flag = flag;
        this.gender = gender;
        this.name = name;
        this.profileImage = profileImage;
        this.surname = surname;
        this.userID = userID;
        this.username = username;
        this.premium = premium;
        this.favoritePostClub = favoritePostClub;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFavoriteClub() {
        return favoriteClub;
    }

    public void setFavoriteClub(String favoriteClub) {
        this.favoriteClub = favoriteClub;
    }

    public String getFavoriteClubLogo() {
        return favoriteClubLogo;
    }

    public void setFavoriteClubLogo(String favoriteClubLogo) {
        this.favoriteClubLogo = favoriteClubLogo;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getFavoritePostClub() {
        return favoritePostClub;
    }

    public void setFavoritePostClub(String favoritePostClub) {
        this.favoritePostClub = favoritePostClub;
    }
}
