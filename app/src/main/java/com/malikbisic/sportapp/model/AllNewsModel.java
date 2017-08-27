package com.malikbisic.sportapp.model;

/**
 * Created by Nane on 27.8.2017.
 */

public class AllNewsModel {
    private String title;
    private String shortDesc;
    private String imgsrc;

    public AllNewsModel() {
    }

    public AllNewsModel(String title, String shortDesc, String imgsrc) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.imgsrc = imgsrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
}
