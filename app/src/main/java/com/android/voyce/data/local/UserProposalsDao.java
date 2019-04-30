package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.Proposal;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserProposalsDao {

    @Query("SELECT * FROM proposal where user_id = :id")
    LiveData<List<Proposal>> getProposals(String id);

    @Insert(onConflict = REPLACE)
    void insertProposals(List<Proposal> proposals);

    @Query("DELETE FROM proposal")
    void deleteProposals();
}
