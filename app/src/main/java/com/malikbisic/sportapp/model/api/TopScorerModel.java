package com.malikbisic.sportapp.model.api;

/**
 * Created by korisnik on 02/01/2018.
 */

public class TopScorerModel {

    String name;
    String imagePlayer;
    int goal;
    int position;
    int playerID;

    public TopScorerModel() {
    }

    public TopScorerModel(String name, String imagePlayer, int goal, int position, int playerID) {
        this.name = name;
        this.imagePlayer = imagePlayer;
        this.goal = goal;
        this.position = position;
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePlayer() {
        return imagePlayer;
    }

    public void setImagePlayer(String imagePlayer) {
        this.imagePlayer = imagePlayer;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
