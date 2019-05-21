package com.android.voyce.ui.userprofile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.voyce.R;
import com.android.voyce.databinding.FragmentUserMusicianProfileBinding;
import com.android.voyce.databinding.FragmentUserProfileBinding;
import com.android.voyce.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {


    private String mUserImage;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentUserProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);

        binding.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_navigation_profile_to_userEditFragment);
            }
        });

        binding.followersCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_navigation_profile_to_userFollowingFragment);
            }
        });

        binding.sponsoringCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_navigation_profile_to_userSponsorsFragment);
            }
        });

        binding.settingsCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_navigation_profile_to_userSettingsFragment);
            }
        });

        binding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                binding.getRoot().scrollTo(binding.userProfileImage.getLeft() / 2, 0);
            }
        });
        Picasso.get().load(mUserImage)
                .placeholder(R.drawable.profile_placeholder).into(binding.userProfileImage);

        Picasso.get().load(R.drawable.followers)
                .placeholder(R.drawable.followers).fit().into(binding.followersCircle);

        Picasso.get().load(R.drawable.sponsoring)
                .placeholder(R.drawable.sponsoring).fit().into(binding.sponsoringCircle);

        Picasso.get().load(R.drawable.settings)
                .placeholder(R.drawable.settings).fit().into(binding.settingsCircle);

        Picasso.get().load(R.drawable.playlists)
                .placeholder(R.drawable.playlists).fit().into(binding.playlistsCircle);

        return binding.getRoot();
    }
}
