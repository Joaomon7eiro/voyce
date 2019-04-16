package com.android.voyce.models;

import java.io.Serializable;

public class MusicianMainInfo implements Serializable {
    private String mImageUrl;
    private String mName;
    private String mFollowersNumber;
    private String mSponsorsNumber;
    private String mListenersNumberNumber;

    public MusicianMainInfo(String imageUrl, String name, String listenersNumber, String followersNumber,
                            String sponsorsNumber) {
        mImageUrl = imageUrl;
        mName = name;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumberNumber = listenersNumber;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getFollowersNumber() {
        return mFollowersNumber;
    }

    public String getSponsorsNumber() {
        return mSponsorsNumber;
    }

    public String getListenersNumberNumber() {
        return mListenersNumberNumber;
    }
}
