package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 22/08/2017.
 */

public class Notification {

    String action;
    String uid;


    public Notification() {
    }

    public Notification(String action, String uid) {
        this.action = action;
        this.uid = uid;

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
