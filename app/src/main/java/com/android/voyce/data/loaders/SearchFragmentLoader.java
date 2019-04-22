package com.android.voyce.data.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.android.voyce.data.models.Musician;
import com.android.voyce.utils.NetworkUtils;

import java.util.ArrayList;

public class SearchFragmentLoader extends AsyncTaskLoader<ArrayList<Musician>> {

    private String mUrl;

    public SearchFragmentLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Musician> loadInBackground() {
        return NetworkUtils.fetchMusicianMainInfoData(mUrl);
    }
}
