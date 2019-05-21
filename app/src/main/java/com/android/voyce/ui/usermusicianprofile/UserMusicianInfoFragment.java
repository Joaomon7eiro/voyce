package com.android.voyce.ui.usermusicianprofile;


import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.databinding.FragmentUserMusicianInfoBinding;
import com.android.voyce.databinding.FragmentUserMusicianProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianInfoFragment extends Fragment {

    private FragmentUserMusicianInfoBinding mBinding;

    public UserMusicianInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_musician_info, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserMusicianProfileViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(UserMusicianProfileViewModel.class);
        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBinding.setUser(user);
                }
            }
        });
    }
}
