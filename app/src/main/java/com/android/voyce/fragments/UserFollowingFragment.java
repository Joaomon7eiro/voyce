package com.android.voyce.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.adapters.UserFollowingAdapter;
import com.android.voyce.database.AppDatabase;
import com.android.voyce.database.MusicianModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowingFragment extends Fragment implements UserFollowingAdapter.ListItemClickListener {

    private ArrayList<MusicianModel> mMusicianModels;
    private UserFollowingAdapter mAdapter;

    public UserFollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);

        AppDatabase db = AppDatabase.getInstance(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_following);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new UserFollowingAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        final LiveData<List<MusicianModel>> musicians = db.musicianDao().queryMusicians();
        musicians.observe(this, new Observer<List<MusicianModel>>() {
            @Override
            public void onChanged(@Nullable List<MusicianModel> musicianModels) {
                mMusicianModels = (ArrayList<MusicianModel>) musicianModels;
                mAdapter.setData(mMusicianModels);
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        UserProfileFragment parentFragment = ((UserProfileFragment) getParentFragment());
        if (parentFragment != null) {
            parentFragment.openFragment(MusicianFragment.newInstance(mMusicianModels.get(index).getId()));
        }
    }
}
