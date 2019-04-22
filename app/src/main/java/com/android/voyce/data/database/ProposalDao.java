package com.android.voyce.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.models.ProposalModel;

import java.util.List;

@Dao
public interface ProposalDao {

    @Query("SELECT * from proposals")
    List<ProposalModel> getProposals();

    @Insert
    void insertProposal(ProposalModel proposalModel);

    @Delete
    void deleteProposal(ProposalModel proposalModel);
}