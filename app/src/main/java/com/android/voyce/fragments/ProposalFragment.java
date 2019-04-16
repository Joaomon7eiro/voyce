package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.adapters.ProposalsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProposalFragment extends Fragment{

    RecyclerView mRecyclerView;
    ProposalsAdapter mProposalAdapter;

    public ProposalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);

        mRecyclerView = view.findViewById(R.id.rv_proposals);
        mProposalAdapter = new ProposalsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mProposalAdapter);

        String[] tests = new String[] {"1","1","1","1"};

        mProposalAdapter.setData(tests);

        return view;
    }

}
