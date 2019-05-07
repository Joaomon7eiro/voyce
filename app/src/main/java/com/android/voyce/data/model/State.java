package com.android.voyce.data.model;

import android.support.annotation.NonNull;

public class State {

    private String mId;
    private String mInitials;
    private String mName;

    public State() {
    }

    @NonNull
    @Override
    public String toString() {
        return mInitials;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getInitials() {
        return mInitials;
    }

    public void setInitials(String mInitials) {
        this.mInitials = mInitials;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
