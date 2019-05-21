package com.android.voyce.ui.usermusicianprofile;


import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.databinding.FragmentUserMusicianContactBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianContactFragment extends Fragment {
    private FragmentUserMusicianContactBinding mBinding;

    private Linkify.TransformFilter mTransformFilter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            return "";
        }
    };

    public UserMusicianContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_musician_contact, container, false);

        mBinding.userFacebookUrl.setLinkTextColor(Color.WHITE);
        mBinding.userInstagramUrl.setLinkTextColor(Color.WHITE);
        mBinding.userTwitterUrl.setLinkTextColor(Color.WHITE);

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
                    mBinding.setUserPhoneNumber(user.getPhone_number());

                    Linkify.addLinks(mBinding.userInstagramUrl, Pattern.compile(getString(R.string.instagram)),
                            user.getInstagram_url(), null, mTransformFilter);
                    Linkify.addLinks(mBinding.userFacebookUrl, Pattern.compile(getString(R.string.facebook)),
                            user.getFacebook_url(), null, mTransformFilter);
                    Linkify.addLinks(mBinding.userTwitterUrl, Pattern.compile(getString(R.string.twitter)),
                            user.getTwitter_url(), null, mTransformFilter);
                }
            }
        });
    }
}
