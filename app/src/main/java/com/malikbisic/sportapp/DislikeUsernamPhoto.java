package com.malikbisic.sportapp;

/**
 * Created by korisnik on 08/05/2017.
 */

public class DislikeUsernamPhoto {

    String photoProfile, username;

    public DislikeUsernamPhoto() {
    }

    public DislikeUsernamPhoto(String photoProfile, String username) {
        this.photoProfile = photoProfile;
        this.username = username;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
