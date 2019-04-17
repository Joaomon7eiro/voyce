package com.android.voyce.models;

public class Proposal {

    private String mName;
    private String mImageUrl;
    private String mDescription;
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
