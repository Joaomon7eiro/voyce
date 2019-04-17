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
import com.android.voyce.models.Musician;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowingFragment extends Fragment {


    public UserFollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_following);
        UserFollowingAdapter adapter = new UserFollowingAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ArrayList<Musician> musicians = new ArrayList<>();

        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));


        adapter.setData(musicians);
        return view;
    }

}
