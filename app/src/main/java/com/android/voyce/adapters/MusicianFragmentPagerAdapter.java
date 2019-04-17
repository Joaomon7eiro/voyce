package com.android.voyce.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.voyce.fragments.MusicianInfoFragment;
import com.android.voyce.fragments.ProposalFragment;
import com.android.voyce.models.MusicianAndProposals;

public class MusicianFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabsTitle;
    private MusicianAndProposals mMusicianAndProposals;

    public MusicianFragmentPagerAdapter(FragmentManager fragmentManager,String[] tabsTitle,
                                        MusicianAndProposals musicianAndProposals) {
        super(fragmentManager);
        mTabsTitle = tabsTitle;
        mMusicianAndProposals = musicianAndProposals;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MusicianInfoFragment.newInstance(mMusicianAndProposals.getMusician());
            case 1:
                return ProposalFragment.newInstance(mMusicianAndProposals);
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
