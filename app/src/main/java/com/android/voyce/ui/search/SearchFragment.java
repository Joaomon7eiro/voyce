package com.android.voyce.ui.search;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.data.model.User;
import com.android.voyce.R;
import com.android.voyce.databinding.FragmentSearchBinding;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements
        MusiciansAdapter.RecyclerViewItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MusiciansAdapter mMusiciansAdapter;
    private MusiciansAdapter mCityMusiciansAdapter;
    private MusiciansAdapter mStateMusiciansAdapter;

    private SearchViewModel mViewModel;
    private FragmentSearchBinding mBinding;

    private String mUserCity;
    private String mUserState;

    private static final long REFRESH_TIME = 60000;

    private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openSearchResults();
        }
    };

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserCity = sharedPreferences.getString(Constants.KEY_CURRENT_USER_CITY, null);
        mUserState = sharedPreferences.getString(Constants.KEY_CURRENT_USER_STATE, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_search, container, false);

        mBinding.searchScrollContainer.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean isCollapsed = true;

            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldX, int oldY) {
                if (scrollY == 0 && oldY > 0) {
                    if (!isCollapsed) {
                        mBinding.scalingLayout.collapse();
                        isCollapsed = true;
                        mBinding.swipeRefresh.setEnabled(true);
                    }
                } else {
                    if (isCollapsed) {
                        mBinding.swipeRefresh.setEnabled(false);
                        mBinding.scalingLayout.expand();
                        isCollapsed = false;
                    }
                }
            }
        });

        mBinding.searchContainer.setOnClickListener(mSearchClickListener);

        mBinding.swipeRefresh.setOnRefreshListener(this);
        mBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerCity = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerState = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_GENERAL);
        mCityMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_CITY);
        mStateMusiciansAdapter = new MusiciansAdapter(this, Constants.ADAPTER_STATE);

        mBinding.mainArtistsRv.setLayoutManager(layoutManager);
        mBinding.mainArtistsRv.setNestedScrollingEnabled(false);
        mBinding.mainArtistsRv.setHasFixedSize(true);
        mBinding.mainArtistsRv.setAdapter(mMusiciansAdapter);

        mBinding.localArtistsRv.setLayoutManager(layoutManagerCity);
        mBinding.localArtistsRv.setNestedScrollingEnabled(false);
        mBinding.localArtistsRv.setHasFixedSize(true);
        mBinding.localArtistsRv.setAdapter(mCityMusiciansAdapter);

        mBinding.regionArtistsRv.setLayoutManager(layoutManagerState);
        mBinding.regionArtistsRv.setNestedScrollingEnabled(false);
        mBinding.regionArtistsRv.setHasFixedSize(true);
        mBinding.regionArtistsRv.setAdapter(mStateMusiciansAdapter);

        return mBinding.getRoot();
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
                    if (mBinding.onVoyceLabel.getVisibility() == View.GONE) {
                        mBinding.onVoyceLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mMusiciansAdapter.getItemCount() < 1) {
                        mBinding.onVoyceLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getCityMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null && users.size() > 0) {
                    mCityMusiciansAdapter.setData(users);
                    if (mBinding.localArtistsLabel.getVisibility() == View.GONE) {
                        mBinding.localArtistsLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mCityMusiciansAdapter.getItemCount() < 1) {
                        mBinding.localArtistsLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getStateMusicians().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null && users.size() > 0) {
                    mStateMusiciansAdapter.setData(users);
                    if (mBinding.regionArtistsLabel.getVisibility() == View.GONE) {
                        mBinding.regionArtistsLabel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mStateMusiciansAdapter.getItemCount() < 1) {
                        mBinding.regionArtistsLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mBinding.swipeRefresh.setRefreshing(true);
                } else {
                    mBinding.swipeRefresh.setRefreshing(false);
                }
            }
        });
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

                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        if (ConnectivityHelper.isConnected(getContext())) {
            mViewModel.refreshData(REFRESH_TIME);
        } else {
            mBinding.swipeRefresh.setRefreshing(false);
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    public void openSearchResults() {
        Navigation.findNavController(mBinding.getRoot())
                .navigate(R.id.action_navigation_search_to_searchResultsFragment2);
    }
}
