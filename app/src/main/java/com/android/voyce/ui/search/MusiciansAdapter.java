package com.android.voyce.ui.search;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.databinding.MusiciansListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MusiciansAdapter extends RecyclerView.Adapter<MusiciansAdapter.MusiciansAdapterViewHolder> {

    private final RecyclerViewItemClickListener mOnClickListener;
    private List<User> mMusiciansData = new ArrayList<>();
    private String mAdapterName;

    MusiciansAdapter(RecyclerViewItemClickListener listItemClickListener, String adapterName) {
        mOnClickListener = listItemClickListener;
        mAdapterName = adapterName;
    }

    @NonNull
    @Override
    public MusiciansAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MusiciansListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.musicians_list_item,
                viewGroup, false);
        return new MusiciansAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusiciansAdapterViewHolder viewHolder, int i) {
        User musician = mMusiciansData.get(i);
        if (musician != null) {
            viewHolder.bindTo(musician);
        }
    }

    @Override
    public int getItemCount() {
        if (mMusiciansData == null) return 0;
        return mMusiciansData.size();
    }


    class MusiciansAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MusiciansListItemBinding mBinding;

        private MusiciansAdapterViewHolder(@NonNull MusiciansListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition(), mAdapterName);
        }

        void bindTo(User musician) {
            mBinding.setMusician(musician);
            mBinding.executePendingBindings();
        }
    }

    public interface RecyclerViewItemClickListener {
        void onListItemClick(int index, String adapterName);
    }

    public List<User> getData() {
        return mMusiciansData;
    }

    public void setData(List<User> musiciansData) {
        mMusiciansData = musiciansData;
        notifyDataSetChanged();
    }
}
