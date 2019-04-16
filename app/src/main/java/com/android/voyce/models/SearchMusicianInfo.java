package com.android.voyce.models;

public class SearchMusicianInfo {
    private String mImageUrl;
    private String mName;
    private String mFollowersNumber;
    private String mSponsorsNumber;
    private String mListenersNumberNumber;

    public SearchMusicianInfo(String imageUrl, String name, String listenersNumber, String followersNumber,
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
