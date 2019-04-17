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

public class UserSponsoringAdapter extends RecyclerView.Adapter<UserSponsoringAdapter.UserSponsoringAdapterViewHolder> {

    private ArrayList<Musician> mMusicians;

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

    @Override
    public UserSponsoringAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_sponsoring_list_item, viewGroup, false);

        return new UserSponsoringAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserSponsoringAdapterViewHolder viewHolder, int i) {
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
