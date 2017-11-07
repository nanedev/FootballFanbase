package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 04/11/2017.
 */

public class LineUpModel {

    int number;
    String name;
    int playerID;

    public LineUpModel() {
    }

    public LineUpModel(int number, String name, int playerID) {
        this.number = number;
        this.name = name;
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
