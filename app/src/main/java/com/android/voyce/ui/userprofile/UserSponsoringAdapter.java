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
import com.android.voyce.data.model.UserSponsoringProposal;
import com.android.voyce.databinding.UserSponsoringListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UserSponsoringAdapter extends RecyclerView.Adapter<UserSponsoringAdapter.UserSponsoringAdapterViewHolder> {

    private List<UserSponsoringProposal> mProposals = new ArrayList<>();

    private ListItemClickListener mOnListItemClickListener;

    UserSponsoringAdapter(ListItemClickListener listItemClickListener) {
        mOnListItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public UserSponsoringAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        UserSponsoringListItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.user_sponsoring_list_item,
                        viewGroup,
                        false);
        return new UserSponsoringAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSponsoringAdapterViewHolder viewHolder, int i) {
        UserSponsoringProposal userSponsoringProposal = mProposals.get(i);
        if (userSponsoringProposal != null) {
            viewHolder.bindTo(userSponsoringProposal);
        }
    }

    class UserSponsoringAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private UserSponsoringListItemBinding mBinding;

        private UserSponsoringAdapterViewHolder(@NonNull UserSponsoringListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        void bindTo(UserSponsoringProposal userSponsoringProposal) {
            mBinding.setSponsoring(userSponsoringProposal);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        if (mProposals == null) return 0;
        return mProposals.size();
    }

    public void setData(List<UserSponsoringProposal> proposals) {
        mProposals = proposals;
        notifyDataSetChanged();
    }

    public List<UserSponsoringProposal> getData() {
        return mProposals;
    }
}
