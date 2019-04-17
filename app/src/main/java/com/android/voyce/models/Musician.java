package com.android.voyce.models;

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
    private int mListenersNumberNumber;

    public Musician(String id, String imageUrl, String name, String biography, int listenersNumber, int followersNumber,
                    int sponsorsNumber, String facebookUrl, String instagramUrl, String twitterUrl) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mBiography = biography;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumberNumber = listenersNumber;
        mFacebookUrl = facebookUrl;
        mInstagramUrl = instagramUrl;
        mTwitterUrl = twitterUrl;
    }

    public Musician(String id, String imageUrl, String name, int listenersNumber, int followersNumber,
                    int sponsorsNumber) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumberNumber = listenersNumber;
    }

    public String getFacebookUrl() {
        return mFacebookUrl;
    }

    public String getId() {
        return mId;
    }

    public String getInstagramUrl() {
        return mInstagramUrl;
    }

    public String getTwitterUrl() {
        return mTwitterUrl;
    }

    public int getListenersNumberNumber() {
        return mListenersNumberNumber;
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
