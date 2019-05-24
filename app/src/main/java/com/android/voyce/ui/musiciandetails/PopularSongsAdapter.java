package com.android.voyce.ui.musiciandetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.voyce.data.model.Song;
import com.android.voyce.databinding.SongListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class PopularSongsAdapter extends RecyclerView.Adapter<PopularSongsAdapter.SongsViewHolder> {

    private List<Song> mSongs = new ArrayList<>();
    private SongListItemClick mListItemClickListener;

    PopularSongsAdapter(SongListItemClick listItemClickListener) {
        mListItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongListItemBinding binding = SongListItemBinding.inflate(inflater);
        return new SongsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder viewHolder, int position) {
        Song song = mSongs.get(position);
        if (song != null) {
            viewHolder.bindTo(song);
        }
    }

    @Override
    public int getItemCount() {
        if (mSongs == null) return 0;
        return mSongs.size();
    }

    class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final SongListItemBinding mBinding;

        SongsViewHolder(@NonNull SongListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        void bindTo(Song song) {
            mBinding.setSong(song);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            mListItemClickListener.onSongListItemClick(getAdapterPosition());
        }
    }

    interface SongListItemClick {
        void onSongListItemClick(int index);
    }

    public List<Song> getData() {
        return mSongs;
    }

    public void setData(List<Song> songs) {
        mSongs = songs;
        notifyDataSetChanged();
    }
}
