package com.android.voyce.data.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class UserSponsoringProposal {

    @PrimaryKey
    @NonNull
    private String id;
    private String sponsor_id;
    private String name;
    private String user_name;
    private double price;
    private String image;
    private String user_image;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
}
