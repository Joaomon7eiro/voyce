package com.android.voyce.ui.usermusicianprofile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;

public class UserMusicianProposalsAdapter extends RecyclerView.Adapter<UserMusicianProposalsAdapter.UserMusicianProposalAdapterViewHolder> {

    String[] mData;

    class UserMusicianProposalAdapterViewHolder extends RecyclerView.ViewHolder {

        public UserMusicianProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public UserMusicianProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proposal_list_item, viewGroup, false);
        return new UserMusicianProposalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMusicianProposalAdapterViewHolder userMusicianProposalAdapterViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public void setData(String[] data) {
        mData = data;
        notifyDataSetChanged();
    }
}
