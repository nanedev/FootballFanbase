package com.malikbisic.sportapp.model.firebase;

/**
 * Created by malikbisic on 17/01/2018.
 */

public class PlayerCommentsModel {

    String uid;
    String text;

    public PlayerCommentsModel() {
    }

    public PlayerCommentsModel(String uid, String text) {
        this.uid = uid;
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
