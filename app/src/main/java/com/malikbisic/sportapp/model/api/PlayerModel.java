package com.malikbisic.sportapp.model.api;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayerModel {

    private  int id;
    private  String name;

    private  String image;

    public PlayerModel() {
    }

    public PlayerModel(int id, String name, String image) {
        this.id = id;
        this.name = name;

        this.image = image;
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
