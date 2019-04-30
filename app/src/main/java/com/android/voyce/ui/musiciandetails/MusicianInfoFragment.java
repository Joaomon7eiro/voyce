package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;



/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {

    private TextView mBiography;

    public MusicianInfoFragment() {
    }

    public static MusicianInfoFragment newInstance() {
        return new MusicianInfoFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MusicianViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(MusicianViewModel.class);
        viewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBiography.setText(user.getBiography());
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician_info, container, false);

        mBiography = view.findViewById(R.id.musician_biography);

        return view;
    }

}
