package com.android.voyce.ui.userprofile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.UserFollowingMusician;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingAdapterViewHolder> {

    private List<UserFollowingMusician> mMusicians = new ArrayList<>();
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
        UserFollowingMusician userFollowingMusician = mMusicians.get(i);
        Picasso.get().load(userFollowingMusician.getImage()).into(viewHolder.mMusicianImage);
        viewHolder.mMusicianName.setText(userFollowingMusician.getName());
    }

    @Override
    public int getItemCount() {
        if (mMusicians == null) return 0;
        return mMusicians.size();
    }

    public void setData(List<UserFollowingMusician> musicians) {
        mMusicians = musicians;
        notifyDataSetChanged();
    }

    public List<UserFollowingMusician> getData() {
        return mMusicians;
    }

}
