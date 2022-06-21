package com.yeslabapps.friendb.model;

public class Block  {

    private String uid;
    private String myId;

    public Block(){

    }

    public Block(String uid, String myId) {
        this.uid = uid;
        this.myId = myId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
