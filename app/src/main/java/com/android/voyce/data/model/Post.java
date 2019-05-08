package com.android.voyce.data.model;

public class Post {

    private String id;
    private String text;
    private String image;
    private String user_image;
    private String user_name;
    private String user_id;
    private long timestamp;

    public Post(String id, String text, String image, String user_image, String user_name, String user_id, long timestamp) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.user_image = user_image;
        this.user_name = user_name;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
