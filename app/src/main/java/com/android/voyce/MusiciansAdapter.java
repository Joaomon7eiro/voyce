package com.android.voyce;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusiciansAdapter extends RecyclerView.Adapter<MusiciansAdapter.MusiciansAdapterViewHolder> {

    private ArrayList<Musician> mMusiciansData;

    @NonNull
    @Override
    public MusiciansAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.musicians_list_item,
                viewGroup, false);
        return new MusiciansAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusiciansAdapterViewHolder musiciansAdapterViewHolder, int i) {
        Musician musician = mMusiciansData.get(i);
        musiciansAdapterViewHolder.mName.setText(musician.getName());
        musiciansAdapterViewHolder.mImage.setImageResource(musician.getProfileImageResourceId());
        musiciansAdapterViewHolder.mFollowers.setText(String.valueOf(musician.getFollowersNumber()));
        musiciansAdapterViewHolder.mSponsors.setText(String.valueOf(musician.getSponsorsNumber()));
    }

    @Override
    public int getItemCount() {
        if (mMusiciansData == null) return 0;
        return mMusiciansData.size();
    }

    public static class MusiciansAdapterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mImage;
        TextView mName;
        TextView mFollowers;
        TextView mSponsors;

        public MusiciansAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.musician_name);
            mImage = itemView.findViewById(R.id.musician_image);
            mFollowers = itemView.findViewById(R.id.musician_followers);
            mSponsors = itemView.findViewById(R.id.musician_sponsors);
        }
    }

    public void setData(ArrayList<Musician> musiciansData) {
        mMusiciansData = musiciansData;
        notifyDataSetChanged();
    }
}
