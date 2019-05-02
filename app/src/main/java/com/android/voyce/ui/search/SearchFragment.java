package com.android.voyce.ui.search;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.voyce.data.model.User;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.musiciandetails.MusicianFragment;
import com.android.voyce.R;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements
        MusiciansAdapter.ListItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private MusiciansAdapter mMusiciansAdapter;

    private ProgressBar mProgressBar;
    private RecyclerView mMusiciansGridRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchViewModel mViewModel;
    private static final long REFRESH_TIME = 60000;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mViewModel.init();

        mViewModel.getMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                mMusiciansAdapter.setData(users);
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) {
                        mMusiciansGridRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mMusiciansGridRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRefreshLayout = view.findViewById(R.id.swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mProgressBar = view.findViewById(R.id.search_progress_bar);

        mMusiciansGridRecyclerView = view.findViewById(R.id.rv_search_artists_grid);
        mMusiciansAdapter = new MusiciansAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mMusiciansGridRecyclerView.setLayoutManager(layoutManager);

        mMusiciansGridRecyclerView.setHasFixedSize(true);
        mMusiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            User musician = mMusiciansAdapter.getData().get(index);

            MusicianFragment musicianFragment = MusicianFragment.newInstance(musician.getId(),
                    musician.getName(), musician.getImage());

            if (getFragmentManager() != null) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragments_container, musicianFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) activity.setLayoutVisibility(false);
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshData(REFRESH_TIME);
        mRefreshLayout.setRefreshing(false);
    }
}
