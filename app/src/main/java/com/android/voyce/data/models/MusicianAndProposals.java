package com.android.voyce.data.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MusicianAndProposals implements Serializable {
    private Musician mMusician;
    private ArrayList<Proposal> mProposals;

    public MusicianAndProposals(Musician musician, ArrayList<Proposal> proposals) {
        mMusician = musician;
        mProposals = proposals;
    }

    public Musician getMusician() {
        return mMusician;
    }

    public ArrayList<Proposal> getProposals() {
        return mProposals;
    }
}
