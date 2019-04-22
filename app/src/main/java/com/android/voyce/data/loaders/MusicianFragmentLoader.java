package com.android.voyce.data.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.android.voyce.data.models.Musician;
import com.android.voyce.data.models.MusicianAndProposals;
import com.android.voyce.data.models.Proposal;
import com.android.voyce.utils.NetworkUtils;

import java.util.ArrayList;

public class MusicianFragmentLoader extends AsyncTaskLoader<MusicianAndProposals> {

    private String mUrlMusician;
    private String mUrlProposals;

    public MusicianFragmentLoader(Context context, String urlMusician, String urlProposals) {
        super(context);
        mUrlMusician = urlMusician;
        mUrlProposals = urlProposals;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public MusicianAndProposals loadInBackground() {
        Musician musician = NetworkUtils.fetchMusicianDetailsData(mUrlMusician);
        ArrayList<Proposal> proposals = NetworkUtils.fetchMusicianProposalsData(mUrlProposals);

        return new MusicianAndProposals(musician, proposals);
    }
}
