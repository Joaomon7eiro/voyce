package com.android.voyce.ui.userprofile;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UserProfileFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles;

    public UserProfileFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabTitles) {
        super(fragmentManager);
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new UserFollowingFragment();
            case 1:
                return new UserSponsorsFragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
