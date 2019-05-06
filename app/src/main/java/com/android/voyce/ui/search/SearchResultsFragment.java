package com.android.voyce.ui.search;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.musiciandetails.MusicianFragment;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements MusiciansAdapter.ListItemClickListener {


    private ProgressBar mProgressBar;
    private MusiciansAdapter mAdapter;
    private SearchResultsViewModel mViewModel;
    private TextView mResultsLabel;
    private String mUserId;

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SearchResultsViewModel.class);
        mViewModel.init();
        mViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null) {
                    mAdapter.setData(null);
                    if (users.size() > 0) {
                        mResultsLabel.setVisibility(View.VISIBLE);
                        mAdapter.setData(users);
                    } else {
                        mResultsLabel.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        EditText editText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);

        ImageView back = view.findViewById(R.id.search_back_button);
        back.setOnClickListener(mBackOnClickListener);

        mProgressBar = view.findViewById(R.id.search_results_progress);
        mResultsLabel = view.findViewById(R.id.results_label);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.queryUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new MusiciansAdapter(this);

        RecyclerView recyclerView = view.findViewById(R.id.search_results_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            User musician = mAdapter.getData().get(index);
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
}
