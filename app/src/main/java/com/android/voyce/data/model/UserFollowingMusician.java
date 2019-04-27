package com.android.voyce.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class UserFollowingMusician {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String follower_id;
    private String follower_name;
    private String follower_image;

    @NonNull
    private String musician_id;
    private String musician_name;
    private String musician_image;


    public UserFollowingMusician() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(@NonNull String follower_id) {
        this.follower_id = follower_id;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    public String getFollower_image() {
        return follower_image;
    }

    public void setFollower_image(String follower_image) {
        this.follower_image = follower_image;
    }

    @NonNull
    public String getMusician_id() {
        return musician_id;
    }

    public void setMusician_id(@NonNull String musician_id) {
        this.musician_id = musician_id;
    }

    public String getMusician_name() {
        return musician_name;
    }

    public void setMusician_name(String musician_name) {
        this.musician_name = musician_name;
    }

    public String getMusician_image() {
        return musician_image;
    }

    public void setMusician_image(String musician_image) {
        this.musician_image = musician_image;
    }
}
