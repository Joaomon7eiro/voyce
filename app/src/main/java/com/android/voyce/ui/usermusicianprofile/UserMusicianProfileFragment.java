package com.android.voyce.ui.usermusicianprofile;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.LoginTesteActivity;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.Constants;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianProfileFragment extends Fragment {

    private NestedScrollView mContainer;

    private String mUserId;
    private TextView mName;
    private TextView mFollowers;
    private TextView mSponsors;
    private TextView mListeners;
    private TextView mLocation;
    private TextView mGoalValue;
    private TextView mGoalDescription;
    private ImageView mImage;
    private ImageView mBackgroundImage;
    private ProgressBar mGoalProgress;

    private UserMusicianProposalsAdapter mAdapter;

    private LinearLayout mGoalContainer;
    private TextView mNoGoal;

    private RelativeLayout mProposalsContainer;
    private TextView mNoProposals;

    public UserMusicianProfileFragment() {
        // Required empty public constructor
    }

    public static UserMusicianProfileFragment newInstance() {
        UserMusicianProfileFragment fragment = new UserMusicianProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_musician_profile, container, false);

        ImageView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(Constants.KEY_CURRENT_USER_ID, null);
                edit.apply();
                Intent intent = new Intent(getContext(), LoginTesteActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mContainer = view.findViewById(R.id.container_user_musician);

        mGoalContainer = view.findViewById(R.id.goal_container);
        mNoGoal = view.findViewById(R.id.no_goal);

        mProposalsContainer = view.findViewById(R.id.proposals_container);
        mNoProposals = view.findViewById(R.id.no_proposals);

        mName = view.findViewById(R.id.user_musician_name);
        mSponsors = view.findViewById(R.id.user_musician_sponsors_number);
        mFollowers = view.findViewById(R.id.user_musician_followers_number);
        mListeners = view.findViewById(R.id.user_musician_listeners_number);
        mLocation = view.findViewById(R.id.user_musician_location);
        mGoalValue = view.findViewById(R.id.user_musician_goal_value);
        mGoalDescription = view.findViewById(R.id.goal_description);
        mGoalProgress = view.findViewById(R.id.sb_goal_progress);
        mImage = view.findViewById(R.id.user_musician_profile_image);
        mBackgroundImage = view.findViewById(R.id.user_musician_background);

        String[] tabsTitle = new String[]{"Info", "Contato"};
        UserMusicianProfileFragmentPagerAdapter pagerAdapter =
                new UserMusicianProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitle);

        ViewPager viewPager = view.findViewById(R.id.user_musician_view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.user_musician_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        mAdapter = new UserMusicianProposalsAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.rv_user_musician_proposals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);

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
                    mFollowers.setText(String.valueOf(user.getFollowers()));
                    mSponsors.setText((String.valueOf(user.getSponsors())));
                    mListeners.setText((String.valueOf(user.getListeners())));
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
                    String currentGoalValue = String.valueOf(goal.getCurrent_value());
                    mGoalValue.setText(getString(R.string.user_goal, currentGoalValue, goalValue));
                    mGoalDescription.setText(goal.getDescription());
                    int progress = (int) (goal.getCurrent_value() * 100 / goal.getValue());
                    mGoalProgress.setProgress(progress);
                    mNoGoal.setVisibility(View.GONE);
                    mGoalContainer.setVisibility(View.VISIBLE);
                } else {
                    mGoalContainer.setVisibility(View.GONE);
                    mNoGoal.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
            @Override
            public void onChanged(@Nullable List<Proposal> proposals) {
               if (proposals != null && proposals.size() >= 1) {
                    mProposalsContainer.setVisibility(View.VISIBLE);
                    mNoProposals.setVisibility(View.GONE);
                } else {
                    mProposalsContainer.setVisibility(View.GONE);
                    mNoProposals.setVisibility(View.VISIBLE);
                }

                mAdapter.setData(proposals);
            }
        });
    }

}
