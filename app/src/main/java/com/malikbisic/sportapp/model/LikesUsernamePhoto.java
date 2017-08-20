package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 08/05/2017.
 */

public class LikesUsernamePhoto {

    String photoProfile, username;

    public LikesUsernamePhoto() {
    }

    public LikesUsernamePhoto(String photoProfile, String username) {
        this.photoProfile = photoProfile;
        this.username = username;
    }

    public String getPhoto() {
        return photoProfile;
    }

    public void setPhoto(String photo) {
        this.photoProfile = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
