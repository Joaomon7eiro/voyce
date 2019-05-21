package com.android.voyce.ui.feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Post;
import com.android.voyce.databinding.FeedListItemBinding;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends PagedListAdapter<Post, FeedAdapter.FeedAdapterViewHolder> {

    private ListItemClickListener mOnListItemClickListener;

    FeedAdapter(@NonNull DiffUtil.ItemCallback<Post> diffCallback,
                ListItemClickListener listItemClickListener) {
        super(diffCallback);
        mOnListItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public FeedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FeedListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.feed_list_item, viewGroup,
                        false);
        return new FeedAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapterViewHolder viewHolder, int i) {
        Post post = getItem(i);
        if (post != null) {
            viewHolder.bindTo(post);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            //viewHolder.clear();
        }
    }

    class FeedAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FeedListItemBinding mBinding;

        FeedAdapterViewHolder(@NonNull FeedListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.postUserImage.setOnClickListener(this);
            mBinding.postUserName.setOnClickListener(this);
        }

        void bindTo(Post post) {
            mBinding.setPost(post);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return super.getItem(position);
    }
}
