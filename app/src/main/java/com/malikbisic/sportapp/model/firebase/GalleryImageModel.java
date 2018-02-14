package com.malikbisic.sportapp.model.firebase;

/**
 * Created by malikbisic on 14/02/2018.
 */

public class GalleryImageModel {
    String image;

    public GalleryImageModel() {
    }

    public GalleryImageModel(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
