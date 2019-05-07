package com.android.voyce.ui.search;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
    private MusiciansAdapter mCityMusiciansAdapter;
    private MusiciansAdapter mStateMusiciansAdapter;

    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchViewModel mViewModel;
    private LinearLayout mRecyclersViewsContainer;

    private String mUserCity;
    private String mUserState;

    private Handler mShowResultsHandler = new Handler();

    private static final long REFRESH_TIME = 60000;
    private static final int RESULTS_DELAY = 300;

    public SearchFragment() {
    }

    public static SearchFragment newInstance(String userCity, String userState) {
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_CURRENT_USER_CITY, userCity);
        args.putString(Constants.KEY_CURRENT_USER_STATE, userState);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUserCity = args.getString(Constants.KEY_CURRENT_USER_CITY, null);
            mUserState = args.getString(Constants.KEY_CURRENT_USER_STATE, null);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            mUserCity = sharedPreferences.getString(Constants.KEY_CURRENT_USER_CITY, null);
            mUserState = sharedPreferences.getString(Constants.KEY_CURRENT_USER_STATE, null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mViewModel.init(mUserCity, mUserState);

        mViewModel.getMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                mMusiciansAdapter.setData(users);
            }
        });

        mViewModel.getCityMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                mCityMusiciansAdapter.setData(users);
            }
        });

        mViewModel.getStateMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                mStateMusiciansAdapter.setData(users);
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading && mRecyclersViewsContainer.getVisibility() == View.VISIBLE) {
                        mRecyclersViewsContainer.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);

                    } else if (mRecyclersViewsContainer.getVisibility() == View.GONE) {
                        mShowResultsHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mRecyclersViewsContainer.setVisibility(View.VISIBLE);
                            }
                        }, RESULTS_DELAY);
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
        mProgressBar = view.findViewById(R.id.search_progress_bar);

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerCity = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerState = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_GENERAL);
        mCityMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_CITY);
        mStateMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_STATE);

        RecyclerView musiciansRv = view.findViewById(R.id.main_artists_rv);
        musiciansRv.setLayoutManager(layoutManager);
        musiciansRv.setNestedScrollingEnabled(false);
        musiciansRv.setHasFixedSize(true);
        musiciansRv.setAdapter(mMusiciansAdapter);

        RecyclerView cityMusiciansRv = view.findViewById(R.id.local_artists_rv);
        cityMusiciansRv.setLayoutManager(layoutManagerCity);
        cityMusiciansRv.setNestedScrollingEnabled(false);
        cityMusiciansRv.setHasFixedSize(true);
        cityMusiciansRv.setAdapter(mCityMusiciansAdapter);

        RecyclerView stateMusiciansRv = view.findViewById(R.id.region_artists_rv);
        stateMusiciansRv.setLayoutManager(layoutManagerState);
        stateMusiciansRv.setNestedScrollingEnabled(false);
        stateMusiciansRv.setHasFixedSize(true);
        stateMusiciansRv.setAdapter(mStateMusiciansAdapter);

        return view;
    }

    @Override
    public void onListItemClick(int index, String adapterName) {
        if (ConnectivityHelper.isConnected(getContext())) {
            User musician = null;
            switch (adapterName) {
                case Constants.ADAPTER_GENERAL:
                    musician = mMusiciansAdapter.getData().get(index);
                    break;
                case Constants.ADAPTER_CITY:
                    musician = mCityMusiciansAdapter.getData().get(index);
                    break;
                case Constants.ADAPTER_STATE:
                    musician = mStateMusiciansAdapter.getData().get(index);
                    break;
                default:
            }
            if (musician != null) {
                MusicianFragment musicianFragment = MusicianFragment.newInstance(musician.getId(),
                        musician.getName(), musician.getImage());

                if (getFragmentManager() != null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragments_container, musicianFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
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
