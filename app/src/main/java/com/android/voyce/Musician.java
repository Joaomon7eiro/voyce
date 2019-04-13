package com.android.voyce;

public class Musician {
    private int mProfileImageResourceId;
    private String mName;
    private String mDescription;
    private int mFollowersNumber;
    private int mSponsorsNumber;
    private int mListenersNumberNumber;

    public Musician(int profileImageResourceId, String name, String description, int listenersNumber, int followersNumber,
                    int sponsorsNumber) {
        mProfileImageResourceId = profileImageResourceId;
        mName = name;
        mDescription = description;
        mFollowersNumber = followersNumber;
        mSponsorsNumber = sponsorsNumber;
        mListenersNumberNumber = listenersNumber;
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

    public int getProfileImageResourceId() {
        return mProfileImageResourceId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }
}
