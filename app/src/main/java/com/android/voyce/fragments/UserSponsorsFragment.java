package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.adapters.UserSponsoringAdapter;
import com.android.voyce.models.Musician;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSponsorsFragment extends Fragment {

    RecyclerView mRecyclerView;
    UserSponsoringAdapter mAdapter;

    public UserSponsorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sponsors, container, false);

        mRecyclerView = view.findViewById(R.id.rv_user_sponsoring);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new UserSponsoringAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ArrayList<Musician> musicians = new ArrayList<>();

        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));

        mAdapter.setData(musicians);

        return view;
    }

}
