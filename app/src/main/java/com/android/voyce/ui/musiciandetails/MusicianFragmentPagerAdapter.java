package com.android.voyce.ui.musiciandetails;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MusicianFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;

    MusicianFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabsTitle) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabsTitle = tabsTitle;
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MusicianInfoFragment.newInstance();
            case 1:
                return MusicianContactFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
