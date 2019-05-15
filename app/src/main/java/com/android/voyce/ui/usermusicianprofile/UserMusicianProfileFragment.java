package com.android.voyce.ui.usermusicianprofile;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.ui.newpost.NewPostActivity;
import com.android.voyce.ui.userprofile.UserFollowingAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private TextView mBecameMusician;
    private int mUserType;
    private UserMusicianProfileViewModel mViewModel;

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
        mUserType = sharedPreferences.getInt(Constants.KEY_CURRENT_USER_TYPE, 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_musician_profile, container, false);

        mContainer = view.findViewById(R.id.container_user_musician);
        mBecameMusician = view.findViewById(R.id.became_musician_button);

        TextView followersLabel = view.findViewById(R.id.followers_label);
        followersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserFollowingAdapter adapter = new UserFollowingAdapter(null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = LayoutInflater
                        .from(getContext())
                        .inflate(R.layout.followers_sponsors_listeners_dialog,
                                null,
                                false);
                RecyclerView listRecyclerView = dialogView.findViewById(R.id.followers_sponsors_listeners_rv);
                listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                listRecyclerView.setAdapter(adapter);
                listRecyclerView.setHasFixedSize(true);
                builder.setView(dialogView);
                builder.show();
                mViewModel.getFollowers().observe(getViewLifecycleOwner(), new Observer<List<UserFollowingMusician>>() {
                    @Override
                    public void onChanged(List<UserFollowingMusician> userFollowingMusicians) {
                        adapter.setData(userFollowingMusicians);
                    }
                });
            }
        });

        if (mUserType == 0) {
            mContainer.setVisibility(View.GONE);
            mBecameMusician.setVisibility(View.VISIBLE);
            return view;
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mBecameMusician.setVisibility(View.GONE);
        }

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

        Button newPost = view.findViewById(R.id.new_post);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });

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

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        setupFeedRecyclerView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mUserType != 0) {
            mViewModel = ViewModelProviders.of(this).get(UserMusicianProfileViewModel.class);
            mViewModel.init(mUserId);

            mViewModel.getUserLiveData().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
                        mName.setText(user.getName());
                        mFollowers.setText(String.valueOf(user.getFollowers()));
                        mSponsors.setText((String.valueOf(user.getSponsors())));
                        mListeners.setText((String.valueOf(user.getListeners())));
                        mLocation.setText(getString(R.string.user_location,
                                StringUtils.capitalize(user.getCity()),
                                user.getState().toUpperCase()
                        ));
                        if (user.getImage() != null) {
                            Picasso.get().load(user.getImage())
                                    .placeholder(R.drawable.profile_placeholder).fit().into(mImage);
                        }
                    }
                }
            });

            mViewModel.getGoalLiveData().observe(this, new Observer<Goal>() {
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

            mViewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
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

    private void setupFeedRecyclerView(View view) {
        Query baseQuery = FirebaseFirestore.getInstance()
                .collection("user_posts")
                .document(mUserId)
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

        RecyclerView feedRecyclerView = view.findViewById(R.id.user_musician_feed_rv);
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
