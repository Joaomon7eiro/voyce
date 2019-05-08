package com.android.voyce.ui.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder> {

    private List<Post> postList = new ArrayList<>();

    @NonNull
    @Override
    public FeedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_list_item, viewGroup, false);
        return new FeedAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapterViewHolder viewHolder, int i) {
      //  Post post = postList.get(i);
        //Picasso.get().load(post.getUser_image()).into(viewHolder.mUserImage);
//        if (post.getImage() != null) {
//            Picasso.get().load(post.getImage()).into(viewHolder.mImage);
//        }
//        viewHolder.mText.setText(post.getText());
//        viewHolder.mUserName.setText(post.getUser_name());
//        viewHolder.mTime.setText(String.valueOf(post.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        if (postList.size() == 0) return 0;
        return postList.size();
    }

    class FeedAdapterViewHolder extends RecyclerView.ViewHolder {

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
        }
    }

    public void setData(List<Post> posts) {
        postList = posts;
        notifyDataSetChanged();
    }
}
