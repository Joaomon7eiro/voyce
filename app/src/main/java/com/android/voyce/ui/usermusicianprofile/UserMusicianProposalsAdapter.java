package com.android.voyce.ui.usermusicianprofile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Proposal;

import java.util.ArrayList;
import java.util.List;

public class UserMusicianProposalsAdapter extends RecyclerView.Adapter<UserMusicianProposalsAdapter.UserMusicianProposalAdapterViewHolder> {

    private List<Proposal> mProposals = new ArrayList<>();

    class UserMusicianProposalAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mPrice;
        ImageView mCollapseIcon;
        public UserMusicianProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.proposal_name);
            mPrice = itemView.findViewById(R.id.proposal_price);
            mCollapseIcon = itemView.findViewById(R.id.collapse_icon);
        }
    }

    @NonNull
    @Override
    public UserMusicianProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proposal_list_item, viewGroup, false);
        return new UserMusicianProposalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMusicianProposalAdapterViewHolder viewHolder, int i) {
        Proposal proposal = mProposals.get(i);
        viewHolder.mPrice.setText(String.valueOf(proposal.getPrice()));
        viewHolder.mName.setText(proposal.getName());
        viewHolder.mCollapseIcon.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (mProposals.size() == 0) return 0;
        return mProposals.size();
    }

    public void setData(List<Proposal> proposals) {
        mProposals = proposals;
        notifyDataSetChanged();
    }
}
