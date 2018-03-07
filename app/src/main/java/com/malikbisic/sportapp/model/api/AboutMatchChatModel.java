package com.malikbisic.sportapp.model.api;

import java.util.Date;

/**
 * Created by malikbisic on 07/03/2018.
 */

public class AboutMatchChatModel {

    String uid;
    String textComment;
    Date time;

    public AboutMatchChatModel() {
    }

    public AboutMatchChatModel(String uid, String textComment, Date time) {
        this.uid = uid;
        this.textComment = textComment;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
