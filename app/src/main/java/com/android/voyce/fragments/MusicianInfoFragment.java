package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.models.Musician;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {


    public MusicianInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();

        Musician musician = (Musician) args.getSerializable("musician");

        View view =  inflater.inflate(R.layout.fragment_musician_info, container, false);

        TextView bioText = view.findViewById(R.id.biography_text);

        bioText.setText(musician.getDescription());

        return view;
    }

}
