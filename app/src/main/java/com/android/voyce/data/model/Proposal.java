package com.android.voyce.data.model;

import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class Proposal {
    private String name;
    private String image;
    private String description;
    private double price;

    public Proposal(String name, String imageUrl, String description, double price) {
        this.name = name;
        this.image = imageUrl;
        this.description = description;
        this.price = price;
    }

    @Ignore
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
}
