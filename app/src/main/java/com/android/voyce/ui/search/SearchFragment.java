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
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.voyce.data.model.User;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.musiciandetails.MusicianFragment;
import com.android.voyce.R;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;


import java.util.List;

import iammert.com.view.scalinglib.ScalingLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements
        MusiciansAdapter.ListItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private MusiciansAdapter mMusiciansAdapter;

    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchViewModel mViewModel;
    private static final long REFRESH_TIME = 60000;
    private LinearLayout mRecyclersViewsContainer;

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
                        mRecyclersViewsContainer.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclersViewsContainer.setVisibility(View.VISIBLE);
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

        mRecyclersViewsContainer = view.findViewById(R.id.recyclers_views_container);

        final ScalingLayout scalingLayout = view.findViewById(R.id.scaling_layout);

        NestedScrollView scrollView = view.findViewById(R.id.search_scroll_container);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean isCollapsed = true;

            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldX, int oldY) {
                if (scrollY == 0 && oldY > 0) {
                    if (!isCollapsed) {
                        scalingLayout.collapse();
                        isCollapsed = true;
                        mRefreshLayout.setEnabled(true);
                    }
                } else {
                    if (isCollapsed) {
                        mRefreshLayout.setEnabled(false);
                        scalingLayout.expand();
                        isCollapsed = false;
                    }
                }
            }
        });

        RelativeLayout searchContainer = view.findViewById(R.id.search_container);
        searchContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragments_container, new SearchResultsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mProgressBar = view.findViewById(R.id.search_progress_bar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mMusiciansAdapter = new MusiciansAdapter(this);

        RecyclerView musiciansGridRecyclerView = view.findViewById(R.id.main_artists_rv);
        musiciansGridRecyclerView.setLayoutManager(layoutManager);
        musiciansGridRecyclerView.setNestedScrollingEnabled(false);
        musiciansGridRecyclerView.setHasFixedSize(true);
        musiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        RecyclerView musiciansGridRecyclerView2 = view.findViewById(R.id.local_artists_rv);
        musiciansGridRecyclerView2.setLayoutManager(layoutManager2);
        musiciansGridRecyclerView2.setNestedScrollingEnabled(false);
        musiciansGridRecyclerView2.setHasFixedSize(true);
        musiciansGridRecyclerView2.setAdapter(mMusiciansAdapter);

        RecyclerView musiciansGridRecyclerView3 = view.findViewById(R.id.region_artists_rv);
        musiciansGridRecyclerView3.setLayoutManager(layoutManager3);
        musiciansGridRecyclerView3.setNestedScrollingEnabled(false);
        musiciansGridRecyclerView3.setHasFixedSize(true);
        musiciansGridRecyclerView3.setAdapter(mMusiciansAdapter);

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
