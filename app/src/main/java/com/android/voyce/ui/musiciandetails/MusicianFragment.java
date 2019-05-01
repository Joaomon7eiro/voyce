package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment {

    private Button mFollowButton;

    private String mMusicianId;
    private String mMusicianName;
    private String mMusicianImage;

    private String[] mTabsTitle;

    private MusicianViewModel mViewModel;

    private NestedScrollView mContainer;

    private ImageView mImage;
    private TextView mName;

    private String mUserId;
    private String mSignalId;
    private TextView mFollowers;
    private TextView mSponsors;
    private TextView mListeners;
    private TextView mLocation;
    private TextView mGoalValue;
    private TextView mGoalDescription;
    private ImageView mBackgroundImage;
    private ProgressBar mGoalProgress;

    private ProposalsAdapter mAdapter;
    private ProgressBar mProgressBar;

    private CardView mGoalContainer;
    private CardView mProposalsContainer;

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                if (ConnectivityHelper.isConnected(getContext())) {
                    getFragmentManager().popBackStack();
                } else {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) activity.setLayoutVisibility(false);
                }
            }
        }
    };
    private String mUserName;

    public MusicianFragment() {
    }

    public static MusicianFragment newInstance(String id, String name, String image) {
        MusicianFragment fragment = new MusicianFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_MUSICIAN_ID, id);
        args.putString(Constants.KEY_MUSICIAN_NAME, name);
        args.putString(Constants.KEY_MUSICIAN_IMAGE, image);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mMusicianId = args.getString(Constants.KEY_MUSICIAN_ID);
            mMusicianName = args.getString(Constants.KEY_MUSICIAN_NAME);
            mMusicianImage = args.getString(Constants.KEY_MUSICIAN_IMAGE);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);

        mTabsTitle = new String[]{"Info", "Contato"};
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserFollowingMusician userFollowingMusician = new UserFollowingMusician();
        userFollowingMusician.setId(mMusicianId);
        userFollowingMusician.setImage(mMusicianImage);
        userFollowingMusician.setName(mMusicianName);
        userFollowingMusician.setFollower_id(mUserId);

        mViewModel = ViewModelProviders.of(this).get(MusicianViewModel.class);
        mViewModel.init(userFollowingMusician);
        mViewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mName.setText(user.getName());
                    Picasso.get().load(user.getImage()).into(mImage);
                    mFollowers.setText(String.valueOf(user.getFollowers()));
                    mSponsors.setText(String.valueOf(user.getSponsors()));
                    mListeners.setText(String.valueOf(user.getListeners()));
                    mLocation.setText(getString(R.string.user_location, user.getCity(), user.getState()));
                    mSignalId = user.getSignal_id();
                }
            }
        });

        mViewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
            @Override
            public void onChanged(@Nullable List<Proposal> proposals) {
                if (proposals != null && proposals.size() > 0) {
                    mAdapter.setData(proposals);
                    mProposalsContainer.setVisibility(View.VISIBLE);
                } else {
                    mProposalsContainer.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getGoal().observe(this, new Observer<Goal>() {
            @Override
            public void onChanged(@Nullable Goal goal) {
                if (goal != null) {
                    String goalValue = String.valueOf(goal.getValue());
                    String currentGoalValue = String.valueOf(goal.getCurrent_value());
                    mGoalValue.setText(getString(R.string.user_goal, currentGoalValue, goalValue));
                    mGoalDescription.setText(goal.getDescription());
                    int progress = (int) (goal.getCurrent_value() * 100 / goal.getValue());
                    mGoalProgress.setProgress(progress);
                    mGoalContainer.setVisibility(View.VISIBLE);
                } else {
                    mGoalContainer.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.GONE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mContainer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mViewModel.getIsFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFollowing) {
                if (isFollowing != null) {
                    if (isFollowing) {
                        mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                        mFollowButton.setText(getString(R.string.following));
                    } else {
                        mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                        mFollowButton.setText(getString(R.string.follow));
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician, container, false);

        mContainer = view.findViewById(R.id.container_musician);

        mGoalContainer = view.findViewById(R.id.goal_card);
        mProposalsContainer = view.findViewById(R.id.proposal_card);

        mSponsors = view.findViewById(R.id.musician_sponsors_number);
        mFollowers = view.findViewById(R.id.musician_followers_number);
        mListeners = view.findViewById(R.id.musician_listeners_number);
        mLocation = view.findViewById(R.id.musician_location);
        mGoalValue = view.findViewById(R.id.musician_goal_value);
        mGoalDescription = view.findViewById(R.id.goal_description);
        mGoalProgress = view.findViewById(R.id.sb_goal_progress);
        mImage = view.findViewById(R.id.musician_profile_image);
        mBackgroundImage = view.findViewById(R.id.musician_background);

        ImageView backButton = view.findViewById(R.id.musician_back_button);
        backButton.setOnClickListener(mBackOnClickListener);

        mFollowButton = view.findViewById(R.id.follow_button);
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.handleFollower(mSignalId, mUserName);
            }
        });

        mProgressBar = view.findViewById(R.id.musician_progress_bar);

        ViewPager viewPager = view.findViewById(R.id.musician_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.musician_tab_layout);

        mImage = view.findViewById(R.id.musician_profile_image);
        mName = view.findViewById(R.id.musician_name);

        MusicianFragmentPagerAdapter mPagerAdapter = new MusicianFragmentPagerAdapter(getChildFragmentManager(), mTabsTitle);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        RecyclerView recyclerView = view.findViewById(R.id.rv_musician_proposals);
        mAdapter = new ProposalsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);

        return view;
    }
}
