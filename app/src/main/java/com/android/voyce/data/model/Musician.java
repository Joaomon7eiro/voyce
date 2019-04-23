package com.android.voyce.data.model;

import com.google.gson.annotations.SerializedName;

public class Musician {
    @SerializedName("image")
    private String mImageUrl;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("biography")
    private String mBiography;
    @SerializedName("facebook_url")
    private String mFacebookUrl;
    @SerializedName("instagram_url")
    private String mInstagramUrl;
    @SerializedName("twitter_url")
    private String mTwitterUrl;
    @SerializedName("total_followers")
    private int mFollowersNumber;
    @SerializedName("total_sponsors")
    private int mSponsorsNumber;
    @SerializedName("total_listeners")
    private int mListenersNumber;
    @SerializedName("total_monthly_income")
    private double mMonthlyIncome;

    public Musician(String id, String imageUrl, String name, String biography, int listenersNumber, int followersNumber,
                    int sponsorsNumber, String facebookUrl, String instagramUrl, String twitterUrl, double monthlyIncome) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mBiography = biography;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumber = listenersNumber;
        mFacebookUrl = facebookUrl;
        mInstagramUrl = instagramUrl;
        mTwitterUrl = twitterUrl;
        mMonthlyIncome = monthlyIncome;
    }

    public Musician(String id, String imageUrl, String name, int listenersNumber, int followersNumber,
                    int sponsorsNumber) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumber = listenersNumber;
    }

    public String getId() {
        return mId;
    }

    public double getMonthlyIncome() {
        return mMonthlyIncome;
    }

    public String getFacebookUrl() {
        return mFacebookUrl;
    }

    public String getInstagramUrl() {
        return mInstagramUrl;
    }

    public String getTwitterUrl() {
        return mTwitterUrl;
    }

    public int getListenersNumber() {
        return mListenersNumber;
    }

    public int getFollowersNumber() {
        return mFollowersNumber;
    }

    public int getSponsorsNumber() {
        return mSponsorsNumber;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getBiography() {
        return mBiography;
    }
}
