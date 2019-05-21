package com.android.voyce.ui.usermusicianprofile;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.databinding.ProposalListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UserMusicianProposalsAdapter extends RecyclerView.Adapter<UserMusicianProposalsAdapter.UserMusicianProposalAdapterViewHolder> {

    private List<Proposal> mProposals = new ArrayList<>();
    private ListItemClickListener mOnListClickListener;

    UserMusicianProposalsAdapter(ListItemClickListener listItemClickListener) {
        mOnListClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public UserMusicianProposalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ProposalListItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.proposal_list_item,
                        viewGroup,
                        false);
        return new UserMusicianProposalAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserMusicianProposalAdapterViewHolder viewHolder, int position) {
        final Proposal proposal = mProposals.get(position);
        if (proposal != null) {
            viewHolder.bindTo(proposal);
        }
    }

    class UserMusicianProposalAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProposalListItemBinding mBinding;

        UserMusicianProposalAdapterViewHolder(@NonNull ProposalListItemBinding binding) {
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
            mOnListClickListener.onListItemClick(getAdapterPosition());
        }
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

    public List<Proposal> getData() {
        return mProposals;
    }
}
