package com.yeslabapps.friendb.model;

public class Avatar {

    private String avatarUrl;
    private String avatarGender;


    public Avatar(){

    }

    public Avatar(String avatarUrl,String avatarGender) {
        this.avatarUrl = avatarUrl;
        this.avatarGender= avatarGender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarGender() {
        return avatarGender;
    }

    public void setAvatarGender(String avatarGender) {
        this.avatarGender = avatarGender;
    }
}


