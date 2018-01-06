package com.malikbisic.sportapp.model;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayerModel {

    private final int id;
    private final String name;

    private final int image;

    public PlayerModel(int id, String name,  int image) {
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


    public int getImage() {
        return image;
    }

}
