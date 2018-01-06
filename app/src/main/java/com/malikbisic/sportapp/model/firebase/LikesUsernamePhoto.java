package com.malikbisic.sportapp.model.firebase;

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
