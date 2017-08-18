package com.malikbisic.sportapp;

/**
 * Created by korisnik on 21/07/2017.
 */

public class Comments {

    String textComment;
    String profileImage;
    String username;

    public Comments() {
    }

    public Comments(String textComment, String profileImage, String username) {
        this.textComment = textComment;
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
