package com.malikbisic.sportapp.model;

/**
 * Created by Nane on 27.8.2017.
 */

public class AllNewsModel {
    private String title;
    private String shortDesc;
    private String imgsrc;
    private String url;

    public AllNewsModel() {
    }

    public AllNewsModel(String title, String shortDesc, String imgsrc,String url) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.imgsrc = imgsrc;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
