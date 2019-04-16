package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.models.Musician;
import com.android.voyce.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {

    Musician mMusician;
    String mBioText = "";

    public MusicianInfoFragment() {
    }

    public static MusicianInfoFragment newInstance(Musician musician) {
        MusicianInfoFragment fragment = new MusicianInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_MUSICIAN, musician);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMusician = (Musician) getArguments().getSerializable(Constants.KEY_MUSICIAN);
        }

        if (mMusician != null) mBioText = mMusician.getDescription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician_info, container, false);

        TextView biography = view.findViewById(R.id.biography_text);
        biography.setText(mBioText);

        return view;
    }

}
