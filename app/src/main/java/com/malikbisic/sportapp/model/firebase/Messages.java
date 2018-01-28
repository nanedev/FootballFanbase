package com.malikbisic.sportapp.model.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Created by Nane on 11.9.2017.
 */

public class Messages {
    private String message,type;
    private Date time;
    private boolean seen;


    private String from;
    @Exclude
    private String key;

    public Messages() {
    }


    public Messages(String message, String type, boolean seen, Date time, String from, String key) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;
        this.key = key;
    }

    public <T extends Messages> T withId(@NonNull final String id) {
        this.key = id;
        return (T) this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
