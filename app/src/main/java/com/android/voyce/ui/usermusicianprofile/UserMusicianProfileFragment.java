package com.android.voyce.ui.usermusicianprofile;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.LoginActivity;
import com.android.voyce.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianProfileFragment extends Fragment {

    private NestedScrollView mContainer;
    private ProgressBar mLoading;
    private ImageView mIconStart;
    private ImageView mIconEnd;

    private String mUserId;
    private TextView mName;
    private TextView mLocation;
    private TextView mGoalValue;
    private TextView mGoalDescription;
    private ImageView mImage;
    private ImageView mBackgroundImage;
    private ProgressBar mGoalProgress;

    private UserMusicianProposalsAdapter mAdapter;
    private RecyclerView mRecyclerView;

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


    public static UserMusicianProfileFragment newInstance(String id) {
        UserMusicianProfileFragment fragment = new UserMusicianProfileFragment();
        Bundle args = new Bundle();
        args.putString("user_id", id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getString("user_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_musician_profile, container, false);

        ImageView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("user_id", null);
                edit.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        mContainer = view.findViewById(R.id.container_user_musician);
        mLoading = view.findViewById(R.id.user_musician_loading);

        mName = view.findViewById(R.id.user_musician_name);
        mLocation = view.findViewById(R.id.user_musician_location);
        mGoalValue = view.findViewById(R.id.user_musician_goal_value);
        mGoalDescription = view.findViewById(R.id.goal_description);
        mGoalProgress = view.findViewById(R.id.sb_goal_progress);
        mImage = view.findViewById(R.id.user_musician_profile_image);
        mBackgroundImage = view.findViewById(R.id.user_musician_background);

        mIconStart = view.findViewById(R.id.icon_start);
        mIconEnd = view.findViewById(R.id.icon_end);

        String[] tabsTitle = new String[]{"Info", "Contato"};
        UserMusicianProfileFragmentPagerAdapter pagerAdapter =
                new UserMusicianProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitle);

        ViewPager viewPager = view.findViewById(R.id.user_musician_view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.user_musician_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        mAdapter = new UserMusicianProposalsAdapter();

        mRecyclerView = view.findViewById(R.id.rv_user_musician_proposals);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);

        ((MainActivity) getActivity()).visibilityEditButton(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserMusicianProfileViewModel viewModel = ViewModelProviders.of(this).get(UserMusicianProfileViewModel.class);

        viewModel.init(mUserId);

        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mName.setText(user.getName());
                    mLocation.setText(getString(R.string.user_location, user.getCity(), user.getState()));
                    mLocation.setText(getString(R.string.user_location, user.getCity(), user.getState()));
                    Picasso.get().load(user.getImage()).into(mImage);
                }
            }
        });

        viewModel.getGoalLiveData().observe(this, new Observer<Goal>() {
            @Override
            public void onChanged(@Nullable Goal goal) {
                if (goal != null) {
                    String goalValue = String.valueOf(goal.getValue());
                    mGoalValue.setText(getString(R.string.user_goal, "0", goalValue));
                    mGoalProgress.setProgress(0);
                    mGoalDescription.setText(goal.getDescription());
                }
            }
        });

        viewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
            @Override
            public void onChanged(@Nullable List<Proposal> proposals) {
                if (proposals.size() > 1) {
                    mIconEnd.setVisibility(View.VISIBLE);
                    mRecyclerView.addOnScrollListener(mOnScrollListener);
                }
                mAdapter.setData(proposals);
            }
        });

        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) {
                        mContainer.setVisibility(View.GONE);
                        mLoading.setVisibility(View.VISIBLE);
                    } else {
                        mContainer.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
