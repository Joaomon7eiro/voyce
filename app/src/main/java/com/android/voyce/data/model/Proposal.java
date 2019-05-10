package com.android.voyce.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class Proposal {

    @NonNull
    @PrimaryKey
    private String id;
    @NonNull
    private String user_id;
    private String name;
    private String image;
    private String description;
    private double price;

    public Proposal() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(@NonNull String user_id) {
        this.user_id = user_id;
    }
}
