package com.android.voyce.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.loaders.SearchFragmentLoader;
import com.android.voyce.adapters.MusiciansAdapter;
import com.android.voyce.R;
import com.android.voyce.models.Musician;
import com.android.voyce.models.MusicianMainInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MusiciansAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<Musician>> {

    RecyclerView mMusiciansGridRecyclerView;

    ArrayList<Musician> mMusicianArrayList = new ArrayList<>();
    MusiciansAdapter mMusiciansAdapter;

    ProgressBar mProgressBar;
    TextView mNoConnectionText;

    private static final int LOADER_ID = 1;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mProgressBar = view.findViewById(R.id.search_progress_bar);
        mNoConnectionText = view.findViewById(R.id.no_network_text);

        mMusiciansGridRecyclerView = view.findViewById(R.id.rv_search_artists_grid);
        mMusiciansAdapter = new MusiciansAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mMusiciansGridRecyclerView.setLayoutManager(layoutManager);

        mMusiciansGridRecyclerView.setHasFixedSize(true);
        mMusiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        Musician musician = mMusicianArrayList.get(index);

        MusicianFragment musicianFragment = MusicianFragment.newInstance(musician);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragments_container, musicianFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public Loader<ArrayList<Musician>> onCreateLoader(int i, Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);

//        String baseUrl = "http://ws.audioscrobbler.com/2.0/";
//        Uri uri = Uri.parse(baseUrl).buildUpon()
//                .appendQueryParameter("method", "chart.gettopartists")
//                .appendQueryParameter("api_key", getString(R.string.api_key))
//                .appendQueryParameter("format", "json")
//                .appendQueryParameter("limit", "12")
//                .build();

        String baseUrl = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/artists";
        Uri uri = Uri.parse(baseUrl).buildUpon()
                .build();

        return new SearchFragmentLoader(getContext(), uri.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Musician>> loader, ArrayList<Musician> musicians) {
        mProgressBar.setVisibility(View.GONE);

        mMusicianArrayList = musicians;
        mMusiciansAdapter.setData(mMusicianArrayList);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Musician>> loader) {
        mMusiciansAdapter.setData(null);
    }
}
