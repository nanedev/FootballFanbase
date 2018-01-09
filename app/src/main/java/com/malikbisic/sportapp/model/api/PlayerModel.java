package com.malikbisic.sportapp.model.api;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayerModel {

    private  int id;
    private  String name;
    private  long points;
    private  String image;

    public PlayerModel() {
    }

    public PlayerModel(int id, String name, String image, long points) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.image = image;
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
