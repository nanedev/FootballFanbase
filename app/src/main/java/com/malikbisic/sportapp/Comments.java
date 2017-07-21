package com.malikbisic.sportapp;

/**
 * Created by korisnik on 21/07/2017.
 */

public class Comments {

    String textComment;
    String profileImage;

    public Comments() {
    }

    public Comments(String textComment, String profileImage) {
        this.textComment = textComment;
        this.profileImage = profileImage;
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
}
