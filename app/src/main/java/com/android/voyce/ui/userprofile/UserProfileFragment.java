package com.android.voyce.ui.userprofile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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


    private String mUserImage;
    private ImageView mImage;

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
        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mImage = view.findViewById(R.id.user_profile_image);
        ImageView followers = view.findViewById(R.id.followers_circle);
        ImageView sponsoring = view.findViewById(R.id.sponsoring_circle);
        ImageView playlists = view.findViewById(R.id.playlists_circle);
        ImageView settings = view.findViewById(R.id.settings_circle);

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragments_container, UserFollowingFragment.newInstance());
                transaction.commit();
            }
        });

        sponsoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragments_container, UserSponsorsFragment.newInstance());
                transaction.commit();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragments_container, new UserSettingsFragment());
                transaction.commit();
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                view.scrollTo(mImage.getLeft() / 2, 0);
            }
        });
        Picasso.get().load(mUserImage).placeholder(R.drawable.profile_placeholder).into(mImage);
        Picasso.get().load(R.drawable.followers).fit().into(followers);
        Picasso.get().load(R.drawable.sponsoring).fit().into(sponsoring);
        Picasso.get().load(R.drawable.settings).fit().into(settings);
        Picasso.get().load(R.drawable.playlists).fit().into(playlists);
        return view;
    }
}
