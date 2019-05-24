package com.android.voyce.ui.usermusicianprofile;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.databinding.FeedListItemBinding;
import com.android.voyce.databinding.FragmentUserMusicianProfileBinding;
import com.android.voyce.databinding.ProposalDialogBinding;
import com.android.voyce.ui.newpost.NewPostActivity;
import com.android.voyce.ui.userprofile.UserFollowingAdapter;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMusicianProfileFragment extends Fragment implements ListItemClickListener {

    private String mUserId;
    private UserMusicianProposalsAdapter mAdapter;
    private int mUserType;
    private UserMusicianProfileViewModel mViewModel;
    private FragmentUserMusicianProfileBinding mBinding;

    public UserMusicianProfileFragment() {
        // Required empty public constructor
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_musician_profile, container, false);

        mBinding.followersLabel.setOnClickListener(new View.OnClickListener() {
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

        mBinding.sponsorsLabel.setOnClickListener(new View.OnClickListener() {
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
                mViewModel.getSponsors().observe(getViewLifecycleOwner(), new Observer<List<UserFollowingMusician>>() {
                    @Override
                    public void onChanged(List<UserFollowingMusician> userFollowingMusicians) {
                        adapter.setData(userFollowingMusicians);
                    }
                });
            }
        });

        if (mUserType == 0) {
            mBinding.containerUserMusician.setVisibility(View.GONE);
            mBinding.becameMusicianButton.setVisibility(View.VISIBLE);
            return mBinding.getRoot();
        } else {
            mBinding.becameMusicianButton.setVisibility(View.GONE);
            mBinding.containerUserMusician.setVisibility(View.VISIBLE);
        }

        mBinding.newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });

        String[] tabsTitle = new String[]{"Info", "Contato"};
        UserMusicianProfileFragmentPagerAdapter pagerAdapter =
                new UserMusicianProfileFragmentPagerAdapter(getChildFragmentManager(), tabsTitle);

        mBinding.userMusicianViewPager.setAdapter(pagerAdapter);

        mBinding.userMusicianTabLayout.setupWithViewPager(mBinding.userMusicianViewPager);

        mAdapter = new UserMusicianProposalsAdapter(this);

        mBinding.rvUserMusicianProposals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvUserMusicianProposals.setAdapter(mAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.rvUserMusicianProposals);

        setupFeedRecyclerView();

        return mBinding.getRoot();
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
                        mBinding.setUser(user);
                        if (user.getImage() != null) {
                            Glide.with(mBinding.getRoot())
                                    .load(user.getImage())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .thumbnail(0.4f)
                                    .into(mBinding.userMusicianProfileImage);
                        }
                    }
                }
            });

            mViewModel.getGoalLiveData().observe(this, new Observer<Goal>() {
                @Override
                public void onChanged(@Nullable Goal goal) {
                    if (goal != null) {
                        mBinding.setGoal(goal);
                        mBinding.goalContainer.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.goalContainer.setVisibility(View.GONE);
                        mBinding.noGoal.setVisibility(View.VISIBLE);
                    }
                }
            });

            mViewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
                @Override
                public void onChanged(@Nullable List<Proposal> proposals) {
                    if (proposals != null && proposals.size() >= 1) {
                        mBinding.proposalsContainer.setVisibility(View.VISIBLE);
                        mBinding.noProposals.setVisibility(View.GONE);
                    } else {
                        mBinding.proposalsContainer.setVisibility(View.GONE);
                        mBinding.noProposals.setVisibility(View.VISIBLE);
                    }

                    mAdapter.setData(proposals);
                }
            });
        }
    }

    private void setupFeedRecyclerView() {
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
                        FeedListItemBinding binding =
                                DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                                        R.layout.feed_list_item,
                                        parent,
                                        false);
                        return new ViewHolder(binding);
                    }
                };

        mBinding.userMusicianFeedRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.userMusicianFeedRv.setAdapter(adapter);
        mBinding.userMusicianFeedRv.setNestedScrollingEnabled(false);
    }

    @Override
    public void onListItemClick(int index) {
        final Proposal proposal = mAdapter.getData().get(index);
        if (proposal != null) {
            LayoutInflater layoutInflater = ((AppCompatActivity) getContext()).getLayoutInflater();
            final ProposalDialogBinding binding = DataBindingUtil
                    .inflate(layoutInflater, R.layout.proposal_dialog, null, false);

            binding.setProposal(proposal);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(binding.getRoot());
            builder.show();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private FeedListItemBinding mBinding;

        private ViewHolder(@NonNull FeedListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        private void bindTo(Post post) {
            mBinding.setPost(post);
            mBinding.executePendingBindings();
        }
    }
}
