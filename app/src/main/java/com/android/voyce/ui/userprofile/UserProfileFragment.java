package com.android.voyce.ui.userprofile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.voyce.R;
import com.android.voyce.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {


    private String mUserId;
    private String mUserImage;
    private ImageView mImage;
    private ImageView mFollowers;
    private ImageView mSponsoring;
    private ImageView mPlaylists;
    private ImageView mSettings;

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
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mImage = view.findViewById(R.id.user_profile_image);
        mFollowers = view.findViewById(R.id.followers_circle);
        mSponsoring = view.findViewById(R.id.sponsoring_circle);
        mPlaylists = view.findViewById(R.id.playlists_circle);
        mSettings = view.findViewById(R.id.settings_circle);

        mFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragments_container, UserFollowingFragment.newInstance());
                transaction.commit();
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                view.scrollTo(mImage.getLeft() / 2, 0);
            }
        });
        Picasso.get().load(mUserImage).into(mImage);

        return view;
    }
}
