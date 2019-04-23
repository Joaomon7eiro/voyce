package com.android.voyce.data.model;

import com.google.gson.annotations.SerializedName;

public class Proposal {
    @SerializedName("name")
    private String mName;
    @SerializedName("image")
    private String mImageUrl;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("price")
    private double mPrice;

    public Proposal(String name, String imageUrl, String description, double price) {
        mName = name;
        mImageUrl = imageUrl;
        mDescription = description;
        mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public double getPrice() {
        return mPrice;
    }
}
