package com.android.voyce.ui.musiciandetails;


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
import com.android.voyce.databinding.FragmentMusicianInfoBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {

    private FragmentMusicianInfoBinding mBinding;

    public MusicianInfoFragment() {
    }

    public static MusicianInfoFragment newInstance() {
        return new MusicianInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_musician_info, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MusicianViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(MusicianViewModel.class);
        viewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBinding.setMusician(user);
                }
            }
        });
    }

}
