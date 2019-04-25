package com.android.voyce.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "musician")
public class Musician {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String mId;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String mImageUrl;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String mName;

    @ColumnInfo(name = "biography")
    @SerializedName("biography")
    private String mBiography;

    @ColumnInfo(name = "facebook_url")
    @SerializedName("facebook_url")
    private String mFacebookUrl;

    @ColumnInfo(name = "instagram_url")
    @SerializedName("instagram_url")
    private String mInstagramUrl;

    @ColumnInfo(name = "twitter_url")
    @SerializedName("twitter_url")
    private String mTwitterUrl;

    @ColumnInfo(name = "total_followers")
    @SerializedName("total_followers")
    private int mFollowersNumber;

    @ColumnInfo(name = "total_sponsors")
    @SerializedName("total_sponsors")
    private int mSponsorsNumber;

    @ColumnInfo(name = "total_listeners")
    @SerializedName("total_listeners")
    private int mListenersNumber;

    @ColumnInfo(name = "total_monthly_income")
    @SerializedName("total_monthly_income")
    private double mMonthlyIncome;

    @ColumnInfo(name = "last_update_timestamp")
    private long mLastUpdateTimestamp;

    public Musician(@NonNull String id, String imageUrl, String name, String biography, int listenersNumber, int followersNumber,
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

    @Ignore
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

    public long getLastUpdateTimestamp() {
        return mLastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long mLastUpdateTimestamp) {
        this.mLastUpdateTimestamp = mLastUpdateTimestamp;
    }

}
