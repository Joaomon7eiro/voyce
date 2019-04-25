package com.android.voyce.ui.usermusicianprofile;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianProfileFragment extends Fragment {

    ImageView mIconStart;
    ImageView mIconEnd;

    public UserMusicianProfileFragment() {
        // Required empty public constructor
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (!recyclerView.canScrollHorizontally(1)) {
                mIconStart.setVisibility(View.VISIBLE);
            } else {
                mIconStart.setVisibility(View.GONE);
            }

            if (!recyclerView.canScrollHorizontally(-1)) {
                mIconEnd.setVisibility(View.VISIBLE);
            } else {
                mIconEnd.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_musician_profile, container, false);

        String[] tabsTitle = new String[] {"Info", "Contato"};

        mIconStart = view.findViewById(R.id.icon_start);
        mIconEnd = view.findViewById(R.id.icon_end);

        UserMusicianProfileFragmentPagerAdapter pagerAdapter =
                new UserMusicianProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitle);

        ViewPager viewPager = view.findViewById(R.id.user_musician_view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.user_musician_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        ((MainActivity) getActivity()).visibilityEditButton(true);

        UserMusicianProposalsAdapter adapter = new UserMusicianProposalsAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_musician_proposals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        String[] fakeData = new String[]{"1", "1", "1", "1"};
        adapter.setData(fakeData);

        recyclerView.addOnScrollListener(mOnScrollListener);

        UserMusicianProfileViewModel viewModel = ViewModelProviders.of(this).get(UserMusicianProfileViewModel.class);

        viewModel.init("TCHpYZ5lrN01XjXFQNik");
        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                TextView name = view.findViewById(R.id.user_musician_name);
                name.setText(user.getName());
            }
        });

        return view;
    }

}
