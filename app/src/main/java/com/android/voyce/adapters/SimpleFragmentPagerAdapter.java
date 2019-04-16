package com.android.voyce.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.voyce.fragments.MusicianInfoFragment;
import com.android.voyce.fragments.ProposalFragment;
import com.android.voyce.models.Musician;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;
    private Musician mMusician;

    public SimpleFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabsTitle, Musician musician) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
        mMusician = musician;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        Bundle args = new Bundle();
        args.putSerializable("musician", mMusician);

        switch (i) {
            case 0:
                fragment = new MusicianInfoFragment();
                fragment.setArguments(args);
                return fragment;
            case 1:
                fragment = new ProposalFragment();
                fragment.setArguments(args);
                return fragment;
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
