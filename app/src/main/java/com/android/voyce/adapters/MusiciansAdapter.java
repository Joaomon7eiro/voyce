package com.android.voyce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.Musician;
import com.android.voyce.R;

import java.util.ArrayList;

public class MusiciansAdapter extends RecyclerView.Adapter<MusiciansAdapter.MusiciansAdapterViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private ArrayList<Musician> mMusiciansData;

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
        Musician musician = mMusiciansData.get(i);
        musiciansAdapterViewHolder.mName.setText(musician.getName());
        musiciansAdapterViewHolder.mImage.setImageResource(musician.getProfileImageResourceId());
        musiciansAdapterViewHolder.mListeners.setText(String.valueOf(musician.getListenersNumberNumber()));
        musiciansAdapterViewHolder.mFollowers.setText(String.valueOf(musician.getFollowersNumber()));
        musiciansAdapterViewHolder.mSponsors.setText(String.valueOf(musician.getSponsorsNumber()));
    }

    @Override
    public int getItemCount() {
        if (mMusiciansData == null) return 0;
        return mMusiciansData.size();
    }

    class MusiciansAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mName;
        TextView mListeners;
        TextView mFollowers;
        TextView mSponsors;

        public MusiciansAdapterViewHolder(@NonNull View itemView) {
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

    public void setData(ArrayList<Musician> musiciansData) {
        mMusiciansData = musiciansData;
        notifyDataSetChanged();
    }
}
