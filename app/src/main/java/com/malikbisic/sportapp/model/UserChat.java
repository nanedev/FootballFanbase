package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UserChat {

    String username;
    String flag;
    String profileImage;
    String userID;

    public UserChat(String username, String flag, String profileImage, String userID) {
        this.username = username;
        this.flag = flag;
        this.profileImage = profileImage;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
