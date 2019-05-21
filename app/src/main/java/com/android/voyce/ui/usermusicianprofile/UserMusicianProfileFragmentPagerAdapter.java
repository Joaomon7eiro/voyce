package com.android.voyce.ui.usermusicianprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UserMusicianProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabsTitle;

    UserMusicianProfileFragmentPagerAdapter(FragmentManager fragmentManager, String[] tabsTitle) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabsTitle = tabsTitle;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }

    @NonNull
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
