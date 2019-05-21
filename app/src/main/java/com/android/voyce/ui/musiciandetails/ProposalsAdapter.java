package com.android.voyce.ui.musiciandetails;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
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
import com.android.voyce.databinding.ProposalListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalAdapterViewHolder> {

    private List<Proposal> mProposals = new ArrayList<>();
    private ListItemClickListener mOnClickListener;

    ProposalsAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ProposalListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),R.layout.proposal_list_item,
                viewGroup, false);
        return new ProposalAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProposalAdapterViewHolder viewHolder, int position) {
        Proposal proposal = mProposals.get(position);
        if (proposal != null) {
            viewHolder.bindTo(proposal);
        }
    }

    @Override
    public int getItemCount() {
        if (mProposals == null) return 0;
        return mProposals.size();
    }

    class ProposalAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProposalListItemBinding mBinding;

        private ProposalAdapterViewHolder(@NonNull ProposalListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        void bindTo(Proposal proposal) {
            mBinding.setProposal(proposal);
            mBinding.executePendingBindings();
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
