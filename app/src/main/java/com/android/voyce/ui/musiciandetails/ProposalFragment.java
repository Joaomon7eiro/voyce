package com.android.voyce.ui.musiciandetails;


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
import com.android.voyce.data.models.MusicianAndProposals;
import com.android.voyce.data.models.Proposal;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProposalFragment extends Fragment{

    private ArrayList<Proposal> mProposals;

    public ProposalFragment() {
        // Required empty public constructor
    }

    public static ProposalFragment newInstance(MusicianAndProposals musicianAndProposals) {
        ProposalFragment fragment = new ProposalFragment();

        Bundle args = new Bundle();

        args.putSerializable("musician_and_proposals_key", musicianAndProposals);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.getSerializable("musician_and_proposals_key") != null) {
                MusicianAndProposals musiciansAndProposals = (MusicianAndProposals) args.getSerializable("musician_and_proposals_key");
                if (musiciansAndProposals != null) {
                    mProposals = musiciansAndProposals.getProposals();
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.rv_proposals);
        ProposalsAdapter adapter = new ProposalsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        adapter.setData(mProposals);

        return view;
    }

}
