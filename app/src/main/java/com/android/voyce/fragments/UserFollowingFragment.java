package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.adapters.UserFollowingAdapter;
import com.android.voyce.adapters.UserProfileFragmentPagerAdapter;
import com.android.voyce.models.MusicianMainInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowingFragment extends Fragment {

    RecyclerView mRecyclerView;
    UserFollowingAdapter mAdapter;

    public UserFollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);

        mRecyclerView = view.findViewById(R.id.rv_user_following);
        mAdapter = new UserFollowingAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ArrayList<MusicianMainInfo> musicianMainInfos = new ArrayList<>();

        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));
        musicianMainInfos.add(new MusicianMainInfo("1","1","1","1","1"));

        mAdapter.setData(musicianMainInfos);
        return view;
    }

}
