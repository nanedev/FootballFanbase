package com.malikbisic.sportapp.model.api;

/**
 * Created by korisnik on 30/12/2017.
 */

public class CommentaryMatchModel {

    int minutes;
    String comments;

    public CommentaryMatchModel() {
    }

    public CommentaryMatchModel(int minutes, String comments) {
        this.minutes = minutes;
        this.comments = comments;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
