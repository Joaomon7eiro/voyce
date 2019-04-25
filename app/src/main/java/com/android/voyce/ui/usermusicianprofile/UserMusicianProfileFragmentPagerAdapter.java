package com.android.voyce.ui.usermusicianprofile;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UserMusicianProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabsTitle;

    public UserMusicianProfileFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabsTitle) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new UserMusicianInfoFragment();
            case 1:
                return new UserMusicianContactFragment();
            default:
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
