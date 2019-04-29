package com.android.voyce.ui.userprofile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserSponsoringAdapter extends RecyclerView.Adapter<UserSponsoringAdapter.UserSponsoringAdapterViewHolder> {

    private List<User> mMusicians = new ArrayList<>();

    class UserSponsoringAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView mMusicianImage;
        TextView mMusicianName;
        TextView mProposalName;

        private UserSponsoringAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mMusicianImage = itemView.findViewById(R.id.user_sponsoring_musician_image);
            mMusicianName = itemView.findViewById(R.id.user_sponsoring_musician_name);
            mProposalName = itemView.findViewById(R.id.user_sponsoring_proposal_name);
        }
    }
    @NonNull
    @Override
    public UserSponsoringAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_sponsoring_list_item, viewGroup, false);

        return new UserSponsoringAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSponsoringAdapterViewHolder viewHolder, int i) {
    }

    @Override
    public int getItemCount() {
        if (mMusicians == null) return 0;
        return mMusicians.size();
    }

    public void setData(List<User> musicians) {
        mMusicians = musicians;
        notifyDataSetChanged();
    }
}
