package com.malikbisic.sportapp;

/**
 * Created by Nane on 26.4.2017.
 */

public class Post {
    private String username;
    private String profileImage;
    private String descForAudio;
    private String photoPost;
    private String videoPost;
    private String audioFile;
    private String descForVideo;
    private String descForPhoto;


    public Post() {
    }

    public Post(String username, String profileImage, String descForAudio, String descForVideo, String descForPhoto, String photoPost, String videoPost, String audioFile) {
        this.username = username;
        this.profileImage = profileImage;
        this.descForAudio = descForAudio;
        this.photoPost = photoPost;
        this.videoPost = videoPost;
        this.audioFile = audioFile;
        this.descForPhoto = descForPhoto;
        this.descForVideo = descForVideo;
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

    public String getDescForAudio() {
        return descForAudio;
    }

    public void setDescForAudio(String descForAudio) {
        this.descForAudio = descForAudio;
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

    public String getDescForVideo() {
        return descForVideo;
    }

    public void setDescForVideo(String descForVideo) {
        this.descForVideo = descForVideo;
    }

    public String getDescForPhoto() {
        return descForPhoto;
    }

    public void setDescForPhoto(String descForPhoto) {
        this.descForPhoto = descForPhoto;
    }
}
