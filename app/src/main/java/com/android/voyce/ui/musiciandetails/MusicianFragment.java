package com.android.voyce.ui.musiciandetails;


import android.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Post;
import com.android.voyce.ui.usermusicianprofile.UserMusicianProfileFragment;
import com.android.voyce.utils.StringUtils;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment implements ListItemClickListener {

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
    private String mUserName;
    private String mUserImage;

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

    private boolean mScrollToPlans = false;
    private Handler mScrollHandler;
    private static final int SCROLL_DELAY = 600;
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollTo = mProposalsContainer.getTop();
            mContainer.smoothScrollTo(0, scrollTo);
            mScrollHandler.removeCallbacks(mScrollRunnable);
        }
    };

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

    public MusicianFragment() {
    }

    public static MusicianFragment newInstance(String id, String name, String image, boolean scrollToPlans) {
        MusicianFragment fragment = new MusicianFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_MUSICIAN_ID, id);
        args.putString(Constants.KEY_MUSICIAN_NAME, name);
        args.putString(Constants.KEY_MUSICIAN_IMAGE, image);
        args.putBoolean("scroll", scrollToPlans);

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
            mScrollToPlans = args.getBoolean("scroll");
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);

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
        userFollowingMusician.setFollower_name(mUserName);
        userFollowingMusician.setFollower_image(mUserImage);

        mViewModel = ViewModelProviders.of(this).get(MusicianViewModel.class);
        mViewModel.init(userFollowingMusician);
        mViewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mName.setText(user.getName());
                    if (user.getImage() != null) {
                        Picasso.get().load(user.getImage())
                                .placeholder(R.drawable.profile_placeholder).into(mImage);
                    }
                    mFollowers.setText(String.valueOf(user.getFollowers()));
                    mSponsors.setText(String.valueOf(user.getSponsors()));
                    mListeners.setText(String.valueOf(user.getListeners()));
                    mLocation.setText(getString(R.string.user_location,
                            StringUtils.capitalize(user.getCity()),
                            user.getState().toUpperCase()
                    ));
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
                        if (mScrollToPlans) {
                            mScrollHandler = new Handler();
                            mScrollHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
                        }
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

        if (mUserId.equals(mMusicianId)) {
            getFragmentManager().popBackStack();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragments_container, new UserMusicianProfileFragment())
                    .commit();
        }

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

        ImageButton backButton = view.findViewById(R.id.musician_back_button);
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
        mAdapter = new ProposalsAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        setupFeedRecyclerView(view);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        final Proposal proposal = mAdapter.getData().get(index);
        if (proposal != null) {
            LayoutInflater layoutInflater = ((AppCompatActivity) getContext()).getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.proposal_dialog, null, false);
            TextView name = view.findViewById(R.id.proposal_detail_name);
            TextView price = view.findViewById(R.id.proposal_detail_price);
            TextView description = view.findViewById(R.id.proposal_detail_description);
            final ProgressBar progressBar = view.findViewById(R.id.proposal_loading);
            final Button sponsorButton = view.findViewById(R.id.sponsor_button);

            mViewModel.getIsSponsoring(proposal.getId()).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isSponsoring) {
                    if (isSponsoring != null && isSponsoring) {
                        sponsorButton.setText(getResources().getString(R.string.cancel_plan));
                    } else {
                        sponsorButton.setText(getResources().getString(R.string.sponsor_artist));
                    }
                }
            });
            mViewModel.getProposalLoading().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isLoading) {
                    if (isLoading != null && isLoading) {
                        progressBar.setVisibility(View.VISIBLE);
                        sponsorButton.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        sponsorButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            sponsorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.handleSponsor(mSignalId, mUserName, proposal);
                }
            });

            name.setText(proposal.getName());
            price.setText(String.valueOf(proposal.getPrice()));
            description.setText(proposal.getDescription());

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(view);
            builder.show();
        }
    }

    private void setupFeedRecyclerView(View view) {
        Query baseQuery = FirebaseFirestore.getInstance()
                .collection("user_posts")
                .document(mMusicianId)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        FirestorePagingOptions<Post> options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Post.class)
                .build();

        FirestorePagingAdapter<Post, ViewHolder> adapter =
                new FirestorePagingAdapter<Post, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Post post) {
                        viewHolder.bindTo(post);
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View viewHolder = LayoutInflater.from(getContext()).inflate(R.layout.feed_list_item, parent, false);
                        return new ViewHolder(viewHolder);
                    }
                };

        RecyclerView feedRecyclerView = view.findViewById(R.id.musician_feed_rv);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecyclerView.setAdapter(adapter);
        feedRecyclerView.setNestedScrollingEnabled(false);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mUserImage;
        ImageView mImage;
        TextView mUserName;
        TextView mText;
        TextView mTime;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.post_user_image);
            mUserName = itemView.findViewById(R.id.post_user_name);
            mText = itemView.findViewById(R.id.post_text);
            mImage = itemView.findViewById(R.id.post_image);
            mTime = itemView.findViewById(R.id.post_time);
        }

        private void bindTo(Post post) {
            Picasso.get().load(post.getUser_image()).fit().into(mUserImage);
            if (post.getImage() != null) {
                mImage.setVisibility(View.VISIBLE);
                Picasso.get().load(post.getImage()).into(mImage);
            }
            mText.setText(post.getText());
            mUserName.setText(post.getUser_name());
            mTime.setText(formatDate(post.getTimestamp()));
        }

        private String formatDate(long timestamp) {
            return (String) DateUtils.getRelativeTimeSpanString(timestamp,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        }

    }
}
