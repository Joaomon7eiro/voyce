package com.android.voyce.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.android.voyce.models.Musician;
import com.android.voyce.utils.NetworkUtils;

public class MusicianFragmentLoader extends AsyncTaskLoader<Musician> {

    private String mUrl;

    public MusicianFragmentLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Musician loadInBackground() {
        return NetworkUtils.fetchMusicianDetailsData(mUrl);
    }
}
