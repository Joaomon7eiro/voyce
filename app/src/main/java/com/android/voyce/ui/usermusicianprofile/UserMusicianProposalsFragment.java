package com.android.voyce.ui.usermusicianprofile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianProposalsFragment extends Fragment {


    public UserMusicianProposalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_musician_proposals, container, false);

        UserMusicianProposalsAdapter adapter = new UserMusicianProposalsAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_musician_proposals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        String[] fakeData = new String[]{"1", "1", "1", "1"};
        adapter.setData(fakeData);
        return view;
    }

}
