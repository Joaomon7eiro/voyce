package com.android.voyce.ui.userprofile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.UserSponsoringProposal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserSponsoringAdapter extends RecyclerView.Adapter<UserSponsoringAdapter.UserSponsoringAdapterViewHolder> {

    private List<UserSponsoringProposal> mProposals = new ArrayList<>();

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
    @NonNull
    @Override
    public UserSponsoringAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_sponsoring_list_item, viewGroup, false);

        return new UserSponsoringAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSponsoringAdapterViewHolder viewHolder, int i) {
        UserSponsoringProposal userSponsoringProposal = mProposals.get(i);
        Picasso.get().load(userSponsoringProposal.getUser_image()).into(viewHolder.mMusicianImage);
        viewHolder.mMusicianName.setText(userSponsoringProposal.getUser_name());
        viewHolder.mProposalName.setText(userSponsoringProposal.getName());
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
}
