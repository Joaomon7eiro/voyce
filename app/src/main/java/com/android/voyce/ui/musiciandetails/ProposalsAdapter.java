package com.android.voyce.ui.musiciandetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Proposal;

import java.util.ArrayList;
import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalAdapterViewHolder> {

    private List<Proposal> mProposals = new ArrayList<>();
    private ListItemClickListener mOnClickListener;

    public ProposalsAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proposal_list_item,
                viewGroup, false);

        return new ProposalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProposalAdapterViewHolder viewHolder, int position) {
        final Proposal proposal = mProposals.get(position);
        viewHolder.mPrice.setText(String.valueOf(proposal.getPrice()));
        viewHolder.mName.setText(proposal.getName());
    }

    @Override
    public int getItemCount() {
        if (mProposals == null) return 0;
        return mProposals.size();
    }

    class ProposalAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mName;
        ImageView mImage;
        TextView mPrice;

        private ProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.proposal_name);
            mImage = itemView.findViewById(R.id.proposal_image);
            mPrice = itemView.findViewById(R.id.proposal_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public void setData(List<Proposal> proposals) {
        mProposals = proposals;
        notifyDataSetChanged();
    }

    public List<Proposal> getData() {
        return mProposals;
    }
}
