package com.malikbisic.sportapp.model.firebase;

/**
 * Created by malikbisic on 23/02/2018.
 */

public class UserVoteModel {

    public String userProfileImage;
    public String username;
    public String playerImage;
    public String playerName;
    public String points;

    public UserVoteModel() {
    }

    public UserVoteModel(String userProfileImage, String username, String playerImage, String playerName, String points) {
        this.userProfileImage = userProfileImage;
        this.username = username;
        this.playerImage = playerImage;
        this.playerName = playerName;
        this.points = points;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
