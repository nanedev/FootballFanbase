package com.malikbisic.sportapp.model.api;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayerModel {

    private  int id;
    private  String name;
    private  long points;
    private  String image;
    private String playerID;
    private String numberVotes;

    public PlayerModel() {
    }

    public PlayerModel(int id, String name, long points, String image, String playerID, String numberVotes) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.image = image;
        this.playerID = playerID;
        this.numberVotes = numberVotes;
    }

    public PlayerModel(int id, String name, String image, long points, String playerID) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.image = image;
        this.playerID = playerID;
    }

    public String getNumberVotes() {
        return numberVotes;
    }

    public void setNumbervotes(String numberVotes) {
        this.numberVotes = numberVotes;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getImage() {
        return image;
    }

}
