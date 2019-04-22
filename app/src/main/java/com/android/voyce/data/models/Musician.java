package com.android.voyce.data.models;

import java.io.Serializable;

public class Musician implements Serializable {
    private String mImageUrl;
    private String mId;
    private String mName;
    private String mBiography;
    private String mFacebookUrl;
    private String mInstagramUrl;
    private String mTwitterUrl;
    private int mFollowersNumber;
    private int mSponsorsNumber;
    private int mListenersNumber;
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
