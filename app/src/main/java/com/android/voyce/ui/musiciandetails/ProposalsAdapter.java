package com.android.voyce.ui.musiciandetails;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Proposal;

import java.util.ArrayList;
import java.util.List;

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalAdapterViewHolder> {

    private List<Proposal> mProposals = new ArrayList<>();

    public ProposalsAdapter() {}

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

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = ((AppCompatActivity)viewHolder.itemView.getContext()).getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.proposal_dialog, null, false);
                TextView name = view.findViewById(R.id.proposal_detail_name);
                TextView price = view.findViewById(R.id.proposal_detail_price);
                TextView description = view.findViewById(R.id.proposal_detail_description);

                name.setText(proposal.getName());
                description.setText(proposal.getDescription());
                price.setText(String.valueOf(proposal.getPrice()));

                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                builder.setView(view);
                builder.show();
            }
        });

        viewHolder.mPrice.setText(String.valueOf(proposal.getPrice()));
        viewHolder.mName.setText(proposal.getName());
        viewHolder.mDescription.setText(proposal.getDescription());
    }

    @Override
    public int getItemCount() {
        if (mProposals == null) return 0;
        return mProposals.size();
    }

    class ProposalAdapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mProposalDescriptionContainer;

        TextView mDescription;
        TextView mName;
        ImageView mImage;
        TextView mPrice;

        private ProposalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProposalDescriptionContainer = itemView.findViewById(R.id.proposal_description_container);
            mName = itemView.findViewById(R.id.proposal_name);
            mImage = itemView.findViewById(R.id.proposal_image);
            mPrice = itemView.findViewById(R.id.proposal_price);
            mDescription = itemView.findViewById(R.id.proposal_description);
        }
    }

    public void setData(List<Proposal> proposals) {
        mProposals = proposals;
        notifyDataSetChanged();
    }
}
