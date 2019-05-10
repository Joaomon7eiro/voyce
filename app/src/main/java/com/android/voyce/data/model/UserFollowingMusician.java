package com.android.voyce.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class UserFollowingMusician {

    @PrimaryKey
    @NonNull
    private String id;
    private String follower_id;
    private String name;
    private String image;


    public UserFollowingMusician() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }
}
