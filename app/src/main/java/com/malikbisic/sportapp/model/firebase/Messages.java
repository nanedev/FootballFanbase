package com.malikbisic.sportapp.model.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.Map;

/**
 * Created by Nane on 11.9.2017.
 */

public class Messages {
    private String message;
    private String type;
    private Date time;
    private boolean seen;
    private String from;
    Map<String, String> galleryImage;
    @Exclude
    private String to;

    public <T extends Messages> T withId(@NonNull final String id) {
        this.to = id;
        return (T) this;
    }

    public Messages() {
    }

    public Messages(String message, String type, Date time, boolean seen, String from, String key, Map<String, String> galleryImage) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
        this.to = key;
        this.galleryImage = galleryImage;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(Map<String, String> galleryImage) {
        this.galleryImage = galleryImage;
    }
}
