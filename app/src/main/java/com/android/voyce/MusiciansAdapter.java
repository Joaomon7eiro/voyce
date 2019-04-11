package com.android.voyce;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MusiciansAdapter extends RecyclerView.Adapter<MusiciansAdapter.MusiciansAdapterViewHolder> {

    private String[] mMusiciansData;

    @NonNull
    @Override
    public MusiciansAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.musicians_list_item,
                viewGroup, false);

        MusiciansAdapterViewHolder vh = new MusiciansAdapterViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MusiciansAdapterViewHolder musiciansAdapterViewHolder, int i) {
        musiciansAdapterViewHolder.mArtistsName.setText(mMusiciansData[i]);
    }

    @Override
    public int getItemCount() {
        if (mMusiciansData == null) return 0;
        return mMusiciansData.length;
    }

    public static class MusiciansAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView mArtistsName;

        public MusiciansAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistsName = itemView.findViewById(R.id.musician_name);
        }
    }

    public void setData(String[] musiciansData) {
        mMusiciansData = musiciansData;
        notifyDataSetChanged();
    }
}
