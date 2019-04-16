package com.android.voyce.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.voyce.fragments.MusicianInfoFragment;
import com.android.voyce.fragments.ProposalFragment;
import com.android.voyce.models.Musician;

public class MusicianFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;
    private Musician mMusician;

    public MusicianFragmentPagerAdapter(FragmentManager fragmentManager,String[] tabsTitle,
                                        Musician musician) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
        mMusician = musician;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MusicianInfoFragment.newInstance(mMusician);
            case 1:
                return new ProposalFragment();
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
