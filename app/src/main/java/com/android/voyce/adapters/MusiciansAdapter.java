package com.android.voyce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.models.MusicianMainInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MusiciansAdapter extends RecyclerView.Adapter<MusiciansAdapter.MusiciansAdapterViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private ArrayList<MusicianMainInfo> mMusiciansData;

    public MusiciansAdapter(ListItemClickListener listItemClickListener) {
        mOnClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public MusiciansAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.musicians_list_item,
                viewGroup, false);
        return new MusiciansAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusiciansAdapterViewHolder musiciansAdapterViewHolder, int i) {
        MusicianMainInfo musician = mMusiciansData.get(i);
        musiciansAdapterViewHolder.mName.setText(musician.getName());
        musiciansAdapterViewHolder.mListeners.setText(formatNumber(musician.getListenersNumberNumber()));
        musiciansAdapterViewHolder.mFollowers.setText(formatNumber(musician.getFollowersNumber()));
        musiciansAdapterViewHolder.mSponsors.setText(formatNumber(musician.getSponsorsNumber()));
        Picasso.get().load(musician.getImageUrl()).into(musiciansAdapterViewHolder.mImage);
    }

    @Override
    public int getItemCount() {
        if (mMusiciansData == null) return 0;
        return mMusiciansData.size();
    }

    private String formatNumber(String numberText) {
        int number = Integer.parseInt(numberText);
        String numberString;
        if (Math.abs(number / 1000000) >= 1) {
            numberString = String.valueOf(number / 1000000) + "M";
        } else if (Math.abs(number / 1000) >= 1) {
            numberString = String.valueOf(number / 1000) + "K";
        } else {
            numberString = String.valueOf(number);
        }
        return numberString;
    }

    class MusiciansAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mName;
        TextView mListeners;
        TextView mFollowers;
        TextView mSponsors;

        private MusiciansAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.musician_name);
            mImage = itemView.findViewById(R.id.musician_image);
            mListeners = itemView.findViewById(R.id.listeners);
            mFollowers = itemView.findViewById(R.id.followers);
            mSponsors = itemView.findViewById(R.id.sponsors);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }

    public void setData(ArrayList<MusicianMainInfo> musiciansData) {
        mMusiciansData = musiciansData;
        notifyDataSetChanged();
    }
}
