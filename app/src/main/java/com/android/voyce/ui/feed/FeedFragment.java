package com.android.voyce.ui.feed;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;
import com.android.voyce.ui.newpost.NewPostActivity;
import com.android.voyce.utils.Constants;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private FeedAdapter mAdapter;
    private FeedViewModel mViewModel;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefresh;
    private TextView mNoFeed;
    private Boolean mFirstTimeCreated;
    private int mUserType;

    private DiffUtil.ItemCallback<Post> mDiffCallback = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            if (oldItem.getImage() != null) {
                return oldItem.getText().equals(newItem.getText()) &&
                        oldItem.getImage().equals(newItem.getImage());
            } else {
                return oldItem.getText().equals(newItem.getText());
            }
        }
    };

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(Boolean firstTimeCreated) {
        FeedFragment fragment = new FeedFragment();

        Bundle args = new Bundle();
        args.putBoolean("first_time_created", firstTimeCreated);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mFirstTimeCreated = args.getBoolean("first_time_created", false);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserType = sharedPreferences.getInt(Constants.KEY_CURRENT_USER_TYPE, 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = view.findViewById(R.id.feed_rv);
        mNoFeed = view.findViewById(R.id.no_feed);
        mProgressBar = view.findViewById(R.id.feed_progress_bar);

        mRefresh = view.findViewById(R.id.feed_refresh);
        mRefresh.setOnRefreshListener(this);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mAdapter = new FeedAdapter(mDiffCallback);

        if (mUserType != 0) {
            Button newPost = view.findViewById(R.id.new_post);
            newPost.setVisibility(View.VISIBLE);
            newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NewPostActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        mViewModel.init(mFirstTimeCreated);
        mViewModel.getPostLiveData().observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable PagedList<Post> posts) {
                if (posts != null && posts.size() > 0) {
                    mAdapter.submitList(posts);
                    mNoFeed.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        mNoFeed.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshData(TimeUnit.MINUTES.toMillis(1));
        mRefresh.setRefreshing(false);
    }
}
