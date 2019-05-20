package com.android.voyce.ui.search;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        MusiciansAdapter.RecyclerViewItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MusiciansAdapter mMusiciansAdapter;
    private MusiciansAdapter mCityMusiciansAdapter;
    private MusiciansAdapter mStateMusiciansAdapter;

    private SwipeRefreshLayout mRefreshLayout;
    private SearchViewModel mViewModel;

    private TextView mVoyceLabel;
    private TextView mLocalLabel;
    private TextView mRegionLabel;

    private String mUserCity;
    private String mUserState;
    private View mRootView;

    private static final long REFRESH_TIME = 60000;

    private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openSearchResults();
        }
    };

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
                if (users != null && users.size() > 0) {
                    mMusiciansAdapter.setData(users);
                    if (mVoyceLabel.getVisibility() == View.GONE) {
                        mVoyceLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mMusiciansAdapter.getItemCount() < 1) {
                        mVoyceLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getCityMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null && users.size() > 0) {
                    mCityMusiciansAdapter.setData(users);
                    if (mLocalLabel.getVisibility() == View.GONE) {
                        mLocalLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mCityMusiciansAdapter.getItemCount() < 1) {
                        mLocalLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getStateMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null && users.size() > 0) {
                    mStateMusiciansAdapter.setData(users);
                    if (mRegionLabel.getVisibility() == View.GONE) {
                        mRegionLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mStateMusiciansAdapter.getItemCount() < 1) {
                        mRegionLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mRefreshLayout.setRefreshing(true);
                } else {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        mVoyceLabel = mRootView.findViewById(R.id.on_voyce_label);
        mLocalLabel = mRootView.findViewById(R.id.local_artists_label);
        mRegionLabel = mRootView.findViewById(R.id.region_artists_label);

        final ScalingLayout scalingLayout = mRootView.findViewById(R.id.scaling_layout);

        NestedScrollView scrollView = mRootView.findViewById(R.id.search_scroll_container);
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

        RelativeLayout searchContainer = mRootView.findViewById(R.id.search_container);
        searchContainer.setOnClickListener(mSearchClickListener);

        mRefreshLayout = mRootView.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerCity = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerState = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_GENERAL);
        mCityMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_CITY);
        mStateMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_STATE);

        RecyclerView musiciansRv = mRootView.findViewById(R.id.main_artists_rv);
        musiciansRv.setLayoutManager(layoutManager);
        musiciansRv.setNestedScrollingEnabled(false);
        musiciansRv.setHasFixedSize(true);
        musiciansRv.setAdapter(mMusiciansAdapter);

        RecyclerView cityMusiciansRv = mRootView.findViewById(R.id.local_artists_rv);
        cityMusiciansRv.setLayoutManager(layoutManagerCity);
        cityMusiciansRv.setNestedScrollingEnabled(false);
        cityMusiciansRv.setHasFixedSize(true);
        cityMusiciansRv.setAdapter(mCityMusiciansAdapter);

        RecyclerView stateMusiciansRv = mRootView.findViewById(R.id.region_artists_rv);
        stateMusiciansRv.setLayoutManager(layoutManagerState);
        stateMusiciansRv.setNestedScrollingEnabled(false);
        stateMusiciansRv.setHasFixedSize(true);
        stateMusiciansRv.setAdapter(mStateMusiciansAdapter);

        return mRootView;
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
                SearchFragmentDirections.ActionNavigationSearchToMusicianFragment action =
                        SearchFragmentDirections.actionNavigationSearchToMusicianFragment(
                                musician.getId(),
                                musician.getName(),
                                musician.getImage(),
                                false);

                Navigation.findNavController(mRootView).navigate(action);
            }
        } else {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) activity.setLayoutVisibility(false);
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshData(REFRESH_TIME);
    }

    public void openSearchResults() {
        Navigation.findNavController(mRootView)
                .navigate(R.id.action_navigation_search_to_searchResultsFragment2);
    }
}
