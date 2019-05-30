package com.android.voyce.ui.search;


import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.databinding.FragmentSearchResultsBinding;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements MusiciansAdapter.RecyclerViewItemClickListener {


    private MusiciansAdapter mAdapter;
    private SearchResultsViewModel mViewModel;
    private FragmentSearchResultsBinding mBinding;

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigation.findNavController(mBinding.getRoot()).popBackStack();
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
                if (users != null && users.size() > 0) {
                    mBinding.noSearchResults.setVisibility(View.GONE);
                    mBinding.resultsLabel.setVisibility(View.VISIBLE);
                    mAdapter.setData(users);
                } else {
                    mBinding.resultsLabel.setVisibility(View.GONE);
                    mAdapter.setData(null);
                    mBinding.noSearchResults.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mBinding.searchResultsProgress.setVisibility(View.VISIBLE);
                } else {
                    mBinding.searchResultsProgress.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_results, container, false);

        mBinding.searchView.onActionViewExpanded();
        EditText editText = mBinding.searchView.findViewById(R.id.search_src_text);
        editText.setTextColor(Color.WHITE);

        mBinding.searchBackButton.setOnClickListener(mBackOnClickListener);

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        mAdapter = new MusiciansAdapter(this, Constants.ADAPTER_GENERAL);

        mBinding.searchResultsRv.setLayoutManager(layoutManager);
        mBinding.searchResultsRv.setHasFixedSize(true);
        mBinding.searchResultsRv.setNestedScrollingEnabled(false);
        mBinding.searchResultsRv.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onListItemClick(int index, String adapterName, ImageView view) {
        if (ConnectivityHelper.isConnected(getContext())) {
            User musician = mAdapter.getData().get(index);

            if (musician.getId().equals(FirebaseAuth.getInstance().getUid())) {
                Navigation.findNavController(mBinding.getRoot()).navigate(R.id.action_searchResultsFragment_to_navigation_musician);
            } else {
                SearchResultsFragmentDirections.ActionSearchResultsFragmentToMusicianFragment action =
                        SearchResultsFragmentDirections.actionSearchResultsFragmentToMusicianFragment(
                                musician.getId(),
                                musician.getName(),
                                musician.getImage(),
                                false,
                                String.valueOf(index));
                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}
