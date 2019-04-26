package com.android.voyce.ui.userprofile;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {


    private String mUserId;
    private TextView mName;
    private ImageView mImage;
    private BlurImageView mBackgroundImage;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserProfileViewModel viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.init(mUserId);

        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    Picasso.get().load(user.getImage()).into(mBackgroundImage);
                    Picasso.get().load(user.getImage()).into(mImage);
                    mName.setText(user.getName());
                    mBackgroundImage.setBlur(2);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);

        mImage = view.findViewById(R.id.user_profile_image);
        mName = view.findViewById(R.id.user_name);
        mBackgroundImage = view.findViewById(R.id.user_background_image);

        ViewPager viewPager = view.findViewById(R.id.user_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.user_tab_layout);

        String[] tabsTitles = new String[] {"Seguindo", "Patrocinando"};
        UserProfileFragmentPagerAdapter pagerAdapter = new UserProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitles);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    public void openFragment(Fragment fragment) {
        if (ConnectivityHelper.isConnected(getContext())) {
            if (getFragmentManager() != null) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragments_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) activity.setLayoutVisibility(false);
        }
    }
}
