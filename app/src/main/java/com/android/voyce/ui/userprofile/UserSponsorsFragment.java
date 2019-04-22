package com.android.voyce.ui.userprofile;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.data.models.Musician;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSponsorsFragment extends Fragment {


    public UserSponsorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sponsors, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_sponsoring);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        UserSponsoringAdapter adapter = new UserSponsoringAdapter();
        recyclerView.setAdapter(adapter);

        ArrayList<Musician> musicians = new ArrayList<>();

        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));
        musicians.add(new Musician("1","1","1",1,1,1));

        adapter.setData(musicians);

        return view;
    }

}
