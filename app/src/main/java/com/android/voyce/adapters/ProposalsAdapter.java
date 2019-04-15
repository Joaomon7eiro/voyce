package com.android.voyce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.voyce.R;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalAdapterViewHolder> {

    private String[] mProposalData;
    private int mExpandedPosition = -1;

    public ProposalsAdapter() {
    }

    @NonNull
    @Override
    public ProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proposal_list_item,
                viewGroup, false);

        return new ProposalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProposalAdapterViewHolder proposalHolder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        proposalHolder.mProposalDescriptionContainer.setVisibility(isExpanded? View.VISIBLE: View.GONE);
        proposalHolder.mCollapseIcon.setImageResource(isExpanded? R.drawable.ic_action_drop_up : R.drawable.ic_action_drop_down);
        proposalHolder.itemView.setActivated(isExpanded);
        proposalHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1: position;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mProposalData == null) return 0;
        return mProposalData.length;
    }

    public class ProposalAdapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mProposalDescriptionContainer;
        ImageView mCollapseIcon;
        public ProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProposalDescriptionContainer = itemView.findViewById(R.id.proposal_description_container);
            mCollapseIcon = itemView.findViewById(R.id.collapse_icon);
        }
    }

    public void setData(String[] data) {
        mProposalData = data;
        notifyDataSetChanged();
    }
}
