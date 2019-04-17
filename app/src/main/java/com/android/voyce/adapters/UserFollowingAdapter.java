package com.android.voyce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.models.Musician;

import java.util.ArrayList;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingAdapterViewHolder> {

    private ArrayList<Musician> mMusicians;

    class UserFollowingAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView mMusicianImage;
        TextView mMusicianName;

        private UserFollowingAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mMusicianImage = itemView.findViewById(R.id.user_following_musician_image);
            mMusicianName = itemView.findViewById(R.id.user_following_musician_name);
        }
    }

    @Override
    public UserFollowingAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_following_list_item, viewGroup, false);

        return new UserFollowingAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserFollowingAdapterViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if (mMusicians == null) return 0;
        return mMusicians.size();
    }

    public void setData(ArrayList<Musician> musicians) {
        mMusicians = musicians;
        notifyDataSetChanged();
    }
}
