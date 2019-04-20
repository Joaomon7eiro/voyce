package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.adapters.UserProfileFragmentPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);

        ViewPager viewPager = view.findViewById(R.id.user_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.user_tab_layout);

        String[] tabsTitles = new String[] {"Seguindo", "Patrocinando"};
        UserProfileFragmentPagerAdapter pagerAdapter = new UserProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitles);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    public void openFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragments_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
