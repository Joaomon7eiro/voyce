package com.android.voyce.ui.feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends PagedListAdapter<Post, FeedAdapter.FeedAdapterViewHolder> {

    private ListItemClickListener mOnListItemClickListener;

    public FeedAdapter(@NonNull DiffUtil.ItemCallback<Post> diffCallback,
                       ListItemClickListener listItemClickListener) {
        super(diffCallback);
        mOnListItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public FeedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_list_item, viewGroup, false);
        return new FeedAdapterViewHolder(view);
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
            viewHolder.bindPlaceholder();
        }
    }

    class FeedAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView mUserImage;
        ImageView mImage;
        TextView mUserName;
        TextView mText;
        TextView mTime;

        public FeedAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.post_user_image);
            mUserName = itemView.findViewById(R.id.post_user_name);
            mText = itemView.findViewById(R.id.post_text);
            mImage = itemView.findViewById(R.id.post_image);
            mTime = itemView.findViewById(R.id.post_time);
            mUserImage.setOnClickListener(this);
            mUserName.setOnClickListener(this);
        }

        public void bindTo(Post post) {
            Picasso.get().load(post.getUser_image()).fit().into(mUserImage);
            if (post.getImage() != null) {
                mImage.setVisibility(View.VISIBLE);
                Picasso.get().load(post.getImage()).into(mImage);
            }
            mText.setText(post.getText());
            mUserName.setText(post.getUser_name());
            mTime.setText(formatDate(post.getTimestamp()));
        }

        public void bindPlaceholder() {
            Picasso.get().load(R.drawable.profile_placeholder).into(mUserImage);
            mText.setText("");
            mUserName.setText("");
            mTime.setText("");
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    private String formatDate(long timestamp) {
        return (String) DateUtils.getRelativeTimeSpanString(timestamp,
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return super.getItem(position);
    }
}
