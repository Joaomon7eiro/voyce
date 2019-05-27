package com.android.voyce.ui.feed;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Post;
import com.android.voyce.databinding.FragmentFeedBinding;
import com.android.voyce.ui.newpost.NewPostActivity;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        ListItemClickListener {

    private FragmentFeedBinding mBinding;
    private FeedAdapter mAdapter;
    private FeedViewModel mViewModel;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserType = sharedPreferences.getInt(Constants.KEY_CURRENT_USER_TYPE, -1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mBinding = DataBindingUtil
               .inflate(inflater, R.layout.fragment_feed, container, false);

        mBinding.feedRefresh.setOnRefreshListener(this);
        mBinding.feedRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mAdapter = new FeedAdapter(mDiffCallback, this);

        if (mUserType != 0) {
            mBinding.newPost.setVisibility(View.VISIBLE);
            mBinding.newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NewPostActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mBinding.feedRv.setLayoutManager(layoutManager);
        mBinding.feedRv.setAdapter(mAdapter);
        mBinding.feedRv.setNestedScrollingEnabled(false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        mViewModel.getPostLiveData().observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable PagedList<Post> posts) {
                if (posts != null && posts.size() > 0) {
                    mAdapter.submitList(posts);
                    mBinding.noFeed.setVisibility(View.GONE);
                    mBinding.feedRv.setVisibility(View.VISIBLE);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        mBinding.noFeed.setVisibility(View.VISIBLE);
                        mBinding.feedRv.setVisibility(View.GONE);
                    }
                }
            }
        });
        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    mBinding.feedRefresh.setRefreshing(true);
                } else {
                    mBinding.feedRefresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (ConnectivityHelper.isConnected(getContext())) {
            mViewModel.refreshData(TimeUnit.MINUTES.toMillis(1));
        } else {
            mBinding.feedRefresh.setRefreshing(false);
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    public void scrollToStart() {
        mBinding.feedScroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            Post post = mAdapter.getItem(index);
            if (post != null) {
                FeedFragmentDirections.ActionNavigationFeedToMusicianFragment action =
                        FeedFragmentDirections.actionNavigationFeedToMusicianFragment(
                                post.getUser_id(),
                                post.getUser_name(),
                                post.getUser_image(),
                                false);

                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            mBinding.feedRefresh.setRefreshing(false);
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}
