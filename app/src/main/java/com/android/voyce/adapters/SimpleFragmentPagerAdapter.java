package com.android.voyce.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.voyce.fragments.MusicianInfoFragment;
import com.android.voyce.fragments.ProposalFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;

    public SimpleFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabsTitle) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new ProposalFragment();
            case 1:
                return new MusicianInfoFragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
