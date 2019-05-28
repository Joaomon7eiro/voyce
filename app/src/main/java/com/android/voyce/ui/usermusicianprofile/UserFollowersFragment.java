package com.android.voyce.ui.usermusicianprofile;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.databinding.FragmentUserFollowersBinding;
import com.android.voyce.ui.userprofile.UserFollowingAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowersFragment extends Fragment {


    private UserMusicianProfileViewModel mViewModel;
    private UserFollowingAdapter mAdapter;

    public UserFollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentUserFollowersBinding binding = FragmentUserFollowersBinding.inflate(inflater);

        mAdapter = new UserFollowingAdapter(null);
        binding.followersRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.followersRv.setAdapter(mAdapter);
        binding.followersRv.setHasFixedSize(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserMusicianProfileViewModel.class);
        mViewModel.initFollowers(FirebaseAuth.getInstance().getUid());
        mViewModel.getFollowers().observe(getViewLifecycleOwner(), new Observer<List<UserFollowingMusician>>() {
            @Override
            public void onChanged(List<UserFollowingMusician> userFollowingMusicians) {
                mAdapter.setData(userFollowingMusicians);
            }
        });
    }
}
