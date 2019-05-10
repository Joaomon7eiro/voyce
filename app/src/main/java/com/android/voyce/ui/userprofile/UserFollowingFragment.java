package com.android.voyce.ui.userprofile;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.musiciandetails.MusicianFragment;
import com.android.voyce.utils.ConnectivityHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowingFragment extends Fragment implements UserFollowingAdapter.ListItemClickListener {

    private UserFollowingAdapter mAdapter;
    private TextView mNoFollowing;
    private LinearLayout mContainer;

    public UserFollowingFragment() {
        // Required empty public constructor
    }

    public static UserFollowingFragment newInstance() {
        UserFollowingFragment fragment = new UserFollowingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);
        mNoFollowing = view.findViewById(R.id.no_following);
        mContainer = view.findViewById(R.id.following_container);

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_following);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new UserFollowingAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            if (getFragmentManager() != null) {
                UserFollowingMusician user = mAdapter.getData().get(index);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragments_container, MusicianFragment.newInstance(
                        user.getId(), user.getName(), user.getImage()));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) activity.setLayoutVisibility(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserProfileViewModel viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.init();
        viewModel.getUserFollowingMusicians().observe(this, new Observer<List<UserFollowingMusician>>() {
            @Override
            public void onChanged(@Nullable List<UserFollowingMusician> userFollowingMusicians) {
                if (userFollowingMusicians != null && userFollowingMusicians.size() < 1) {
                    mContainer.setVisibility(View.GONE);
                    mNoFollowing.setVisibility(View.VISIBLE);
                } else {
                    mContainer.setVisibility(View.VISIBLE);
                    mNoFollowing.setVisibility(View.GONE);
                    mAdapter.setData(userFollowingMusicians);
                }
            }
        });
    }
}
