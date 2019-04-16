package com.android.voyce.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.android.voyce.models.SearchMusicianInfo;
import com.android.voyce.utils.NetworkUtils;

import java.util.ArrayList;

public class SearchFragmentLoader extends AsyncTaskLoader<ArrayList<SearchMusicianInfo>> {

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
    public ArrayList<SearchMusicianInfo> loadInBackground() {
        return NetworkUtils.fetchMusicianInfoData(mUrl);
    }
}
