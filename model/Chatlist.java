package com.yeslabapps.friendb.model;

public class Chatlist {
    public String id;
    public String timeList;

    public Chatlist(String id,String timeList) {
        this.id = id;
        this.timeList=timeList;
    }

    public Chatlist() {
    }

    public String getId() {
        return id;
    }

    public String getTimeList() {
        return timeList;
    }

    public void setTimeList(String timeList) {
        this.timeList = timeList;
    }

    public void setId(String id) {
        this.id = id;
    }
}