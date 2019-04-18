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

import com.android.voyce.loaders.SearchFragmentLoader;
import com.android.voyce.adapters.MusiciansAdapter;
import com.android.voyce.R;
import com.android.voyce.models.Musician;
import com.android.voyce.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MusiciansAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<Musician>> {

    private ArrayList<Musician> mMusicianArrayList = new ArrayList<>();
    private MusiciansAdapter mMusiciansAdapter;

    private ProgressBar mProgressBar;

    private static final int LOADER_ID = 1;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mProgressBar = view.findViewById(R.id.search_progress_bar);

        RecyclerView musiciansGridRecyclerView = view.findViewById(R.id.rv_search_artists_grid);
        mMusiciansAdapter = new MusiciansAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        musiciansGridRecyclerView.setLayoutManager(layoutManager);

        musiciansGridRecyclerView.setHasFixedSize(true);
        musiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        Musician musician = mMusicianArrayList.get(index);

        MusicianFragment musicianFragment = MusicianFragment.newInstance(musician);

        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragments_container, musicianFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public Loader<ArrayList<Musician>> onCreateLoader(int i, Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);

        String baseUrl = NetworkUtils.API_BASE_URL + "artists";
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
