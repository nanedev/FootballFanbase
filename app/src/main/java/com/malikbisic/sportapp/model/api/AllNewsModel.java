package com.malikbisic.sportapp.model.api;

/**
 * Created by Nane on 27.8.2017.
 */

public class AllNewsModel {
    private String headline;
    private String trailText;
    private String thumbnail;
    private String bodyText;

    public AllNewsModel() {
    }

    public AllNewsModel(String headline, String trailText, String thumbnail,String bodyText) {
        this.headline = headline;
        this.trailText = trailText;
        this.thumbnail = thumbnail;
        this.bodyText = bodyText;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
