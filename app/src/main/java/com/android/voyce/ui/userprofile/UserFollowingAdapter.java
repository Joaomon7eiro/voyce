package com.android.voyce.ui.userprofile;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.databinding.UserFollowingListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.UserFollowingAdapterViewHolder> {

    private List<UserFollowingMusician> mMusicians = new ArrayList<>();
    private ListItemClickListener mOnClickListener;

    public UserFollowingAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public UserFollowingAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        UserFollowingListItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.user_following_list_item,
                        viewGroup,
                        false);
        return new UserFollowingAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowingAdapterViewHolder viewHolder, int i) {
        UserFollowingMusician userFollowingMusician = mMusicians.get(i);
        if (userFollowingMusician != null) {
            viewHolder.bindTo(userFollowingMusician);
        }
    }

    class UserFollowingAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private UserFollowingListItemBinding mBinding;
        private UserFollowingAdapterViewHolder(@NonNull UserFollowingListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        void bindTo(UserFollowingMusician userFollowingMusician) {
            mBinding.setMusician(userFollowingMusician);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
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
