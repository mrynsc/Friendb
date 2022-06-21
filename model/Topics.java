package com.yeslabapps.friendb.model;

public class Topics {

    private String ownerId;
    private String topic;
    private String topicId;



    public Topics(){

    }


    public Topics(String ownerId, String topic, String topicId) {
        this.ownerId = ownerId;
        this.topic = topic;
        this.topicId = topicId;

    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicId() {
        return topicId;
    }



    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
