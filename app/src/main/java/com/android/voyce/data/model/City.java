package com.android.voyce.data.model;

import android.support.annotation.NonNull;

public class City {
    private String mId;
    private String mName;
    private String mStateId;

    public City() {

    }

    @NonNull
    @Override
    public String toString() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getStateId() {
        return mStateId;
    }

    public void setStateId(String mStateId) {
        this.mStateId = mStateId;
    }
}
