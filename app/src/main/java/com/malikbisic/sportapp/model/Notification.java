package com.malikbisic.sportapp.model;

import java.util.Date;

/**
 * Created by korisnik on 22/08/2017.
 */

public class Notification {

    String action;
    String uid;
    String whatIS;
    Date timestamp;


    public Notification() {
    }

    public Notification(String action, String uid, String whatIS, Date timestamp) {
        this.action = action;
        this.uid = uid;
        this.whatIS = whatIS;
        this.timestamp = timestamp;

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getWhatIS() {
        return whatIS;
    }

    public void setWhatIS(String whatIS) {
        this.whatIS = whatIS;
    }
}
