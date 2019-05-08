package com.android.voyce.ui.userprofile;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.UserSponsoringProposal;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSponsorsFragment extends Fragment {

    private TextView mNoSponsoring;
    private LinearLayout mContainer;
    private UserSponsoringAdapter mAdapter;

    public UserSponsorsFragment() {
        // Required empty public constructor
    }

    public static UserSponsorsFragment newInstance() {
        UserSponsorsFragment fragment = new UserSponsorsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sponsors, container, false);
        mNoSponsoring = view.findViewById(R.id.no_sponsoring);
        mContainer = view.findViewById(R.id.sponsoring_container);

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_sponsoring);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new UserSponsoringAdapter();
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserProfileViewModel viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.init();
        viewModel.getUserSponsoringProposals().observe(this, new Observer<List<UserSponsoringProposal>>() {
            @Override
            public void onChanged(@Nullable List<UserSponsoringProposal> userFollowingMusicians) {
                if (userFollowingMusicians != null && userFollowingMusicians.size() < 1) {
                    mContainer.setVisibility(View.GONE);
                    mNoSponsoring.setVisibility(View.VISIBLE);
                } else {
                    mNoSponsoring.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                    mAdapter.setData(userFollowingMusicians);
                }
            }
        });
    }

}
