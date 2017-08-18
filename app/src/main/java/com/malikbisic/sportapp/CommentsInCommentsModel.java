package com.malikbisic.sportapp;

/**
 * Created by Nane on 18.8.2017.
 */

public class CommentsInCommentsModel {

    String textComment;
    String profileImage;
    String username;

    public CommentsInCommentsModel() {
    }

    public CommentsInCommentsModel(String textComment, String profileImage, String username) {
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
