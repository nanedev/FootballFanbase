package com.malikbisic.sportapp;

/**
 * Created by korisnik on 08/05/2017.
 */

public class LikesUsernamePhoto {

    String photo, username;

    public LikesUsernamePhoto() {
    }

    public LikesUsernamePhoto(String photo, String username) {
        this.photo = photo;
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
