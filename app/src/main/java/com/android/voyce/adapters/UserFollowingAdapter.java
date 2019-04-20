package com.android.voyce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.database.MusicianModel;

import java.util.ArrayList;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingAdapterViewHolder> {

    private ArrayList<MusicianModel> mMusicians;
    private ListItemClickListener mOnClickListener;

    public UserFollowingAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    class UserFollowingAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mMusicianImage;
        TextView mMusicianName;

        private UserFollowingAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mMusicianImage = itemView.findViewById(R.id.user_following_musician_image);
            mMusicianName = itemView.findViewById(R.id.user_following_musician_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public UserFollowingAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_following_list_item, viewGroup, false);

        return new UserFollowingAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowingAdapterViewHolder viewHolder, int i) {
        MusicianModel musicianModel = mMusicians.get(i);
        viewHolder.mMusicianImage.setImageBitmap(musicianModel.getImage());
        viewHolder.mMusicianName.setText(musicianModel.getName());
    }

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }

    @Override
    public int getItemCount() {
        if (mMusicians == null) return 0;
        return mMusicians.size();
    }

    public void setData(ArrayList<MusicianModel> musicians) {
        mMusicians = musicians;
        notifyDataSetChanged();
    }
}
