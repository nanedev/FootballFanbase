package com.malikbisic.sportapp.model.api;

/**
 * Created by korisnik on 27/08/2017.
 */

public class MyClubNews {

    String shortdesc;
    String title;
    String imgsrc;
    String url;


    public MyClubNews(String shortdesc, String title, String imgsrc, String url) {
        this.shortdesc = shortdesc;
        this.title = title;
        this.imgsrc = imgsrc;
        this.url = url;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
