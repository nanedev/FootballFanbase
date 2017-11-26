package com.malikbisic.sportapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Nane on 26.4.2017.
 */
@IgnoreExtraProperties
public class Post {
    private String username;
    private String profileImage;
    private String descForAudio;
    private String photoPost;
    private String videoPost;
    private String audioFile;
    private String descVideo;
    private String descForPhoto;
    private String desc;
    private String uid;
    private String country;
    String clubLogo;
    @Exclude
    private String key;



    public Post() {
    }


    public <T extends Post> T withId(@NonNull final String id) {
        this.key = id;
        return (T) this;
    }

    public Post (String username, String profileImage, String descForAudio, String descVideo, String descForPhoto, String photoPost, String videoPost, String audioFile, String desc, String uid, String country,String clubLogo, String key) {
        this.username = username;
        this.profileImage = profileImage;
        this.descForAudio = descForAudio;
        this.photoPost = photoPost;
        this.videoPost = videoPost;
        this.audioFile = audioFile;
        this.descForPhoto = descForPhoto;
        this.descVideo = descVideo;
        this.desc = desc;
        this.uid = uid;
        this.country = country;
        this.clubLogo = clubLogo;
        this.key = key;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getDescVideo() {
        return descVideo;
    }

    public void setDescVideo(String descVideo) {
        this.descVideo = descVideo;
    }

    public String getDescForPhoto() {
        return descForPhoto;
    }

    public void setDescForPhoto(String descForPhoto) {
        this.descForPhoto = descForPhoto;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }
}
