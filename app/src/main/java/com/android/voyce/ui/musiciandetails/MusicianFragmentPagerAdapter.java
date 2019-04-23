package com.android.voyce.ui.musiciandetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MusicianFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;

    public MusicianFragmentPagerAdapter(FragmentManager fragmentManager,String[] tabsTitle) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MusicianInfoFragment.newInstance();
            case 1:
                return ProposalFragment.newInstance();
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
