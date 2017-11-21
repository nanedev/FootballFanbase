package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UserChat {

    String username;
    String flag;
    String profileImage;
    String userID;
    String date;
    boolean isonline;

    public UserChat(String username, String flag, String profileImage, String userID,String date, boolean isonline) {
        this.username = username;
        this.flag = flag;
        this.profileImage = profileImage;
        this.userID = userID;
        this.date = date;
        this.isonline = isonline;
    }

    public UserChat() {
    }

    public boolean isIsonline() {
        return isonline;
    }

    public void setIsonline(boolean isonline) {
        this.isonline = isonline;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
