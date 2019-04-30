package com.android.voyce.ui.usermusicianprofile;


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
public class UserMusicianInfoFragment extends Fragment {


    private TextView mBiography;

    public UserMusicianInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_musician_info, container, false);

        mBiography = view.findViewById(R.id.user_biography);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserMusicianProfileViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(UserMusicianProfileViewModel.class);
        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBiography.setText(user.getBiography());
                }
            }
        });
    }
}
