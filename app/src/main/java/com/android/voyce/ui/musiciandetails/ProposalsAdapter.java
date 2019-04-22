package com.android.voyce.ui.musiciandetails;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.models.Proposal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalAdapterViewHolder> {

    private ArrayList<Proposal> mProposals;
    private int mExpandedPosition = -1;

    public ProposalsAdapter() {}

    @NonNull
    @Override
    public ProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proposal_list_item,
                viewGroup, false);

        return new ProposalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProposalAdapterViewHolder proposalHolder, final int position) {
        Proposal proposal = mProposals.get(position);
        final boolean isExpanded = position == mExpandedPosition;
        proposalHolder.mProposalDescriptionContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        proposalHolder.mCollapseIcon.setImageResource(isExpanded ? R.drawable.ic_action_drop_up : R.drawable.ic_action_drop_down);
        proposalHolder.itemView.setActivated(isExpanded);
        proposalHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(position);
            }
        });

        Picasso.get().load(proposal.getImageUrl()).into(proposalHolder.mImage);
        proposalHolder.mDescription.setText(proposal.getDescription());
        proposalHolder.mName.setText(proposal.getName());
        proposalHolder.mPrice.setText(String.valueOf(proposal.getPrice()));
    }

    @Override
    public int getItemCount() {
        if (mProposals == null) return 0;
        return mProposals.size();
    }

    class ProposalAdapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mProposalDescriptionContainer;
        ImageView mCollapseIcon;

        TextView mDescription;
        TextView mName;
        ImageView mImage;
        TextView mPrice;

        private ProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProposalDescriptionContainer = itemView.findViewById(R.id.proposal_description_container);
            mCollapseIcon = itemView.findViewById(R.id.collapse_icon);
            mName = itemView.findViewById(R.id.proposal_name);
            mImage = itemView.findViewById(R.id.proposal_image);
            mPrice = itemView.findViewById(R.id.proposal_price);
            mDescription = itemView.findViewById(R.id.proposal_description);
        }
    }

    public void setData(ArrayList<Proposal> proposals) {
        mProposals = proposals;
        notifyDataSetChanged();
    }
}
