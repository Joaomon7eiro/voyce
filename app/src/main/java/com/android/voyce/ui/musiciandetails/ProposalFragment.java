package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.data.model.Proposal;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProposalFragment extends Fragment{

    private ProposalsAdapter mAdapter;

    public ProposalFragment() {
        // Required empty public constructor
    }

    public static ProposalFragment newInstance() {
        return new ProposalFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MusicianViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(MusicianViewModel.class);
//        viewModel.getProposals().observe(getParentFragment(), new Observer<List<Proposal>>() {
//            @Override
//            public void onChanged(@Nullable List<Proposal> proposals) {
//                if (proposals != null) {
//                    mAdapter.setData((ArrayList<Proposal>) proposals);
//                }
//            }
//        });
        viewModel.getProposals().observe(getParentFragment(), new Observer<Proposal>() {
            @Override
            public void onChanged(@Nullable Proposal proposals) {
                if (proposals != null) {
                    ArrayList<Proposal> proposalArrayList = new ArrayList<>();
                    proposalArrayList.add(proposals);
                    mAdapter.setData(proposalArrayList);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_proposals);
        mAdapter = new ProposalsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

}
