package com.malikbisic.sportapp;

/**
 * Created by Nane on 26.4.2017.
 */

public class Post {
    private String username;
    private String profileImage;
    private String desc;
    private String photoPost;
    private String videoPost;
    private String audioFile;

    public Post() {
    }

    public Post(String username, String profileImage, String desc, String photoPost, String videoPost, String audioFile) {
        this.username = username;
        this.profileImage = profileImage;
        this.desc = desc;
        this.photoPost = photoPost;
        this.videoPost = videoPost;
        this.audioFile = audioFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhotoPost() {
        return photoPost;
    }

    public void setPhotoPost(String photoPost) {
        this.photoPost = photoPost;
    }

    public String getVideoPost() {
        return videoPost;
    }

    public void setVideoPost(String videoPost) {
        this.videoPost = videoPost;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }
}
