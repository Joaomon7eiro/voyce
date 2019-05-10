package com.android.voyce.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.voyce.data.model.Proposal;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserProposalsDao {

    @Query("SELECT * FROM proposal where user_id = :id")
    LiveData<List<Proposal>> getProposals(String id);

    @Insert(onConflict = REPLACE)
    void insertProposals(List<Proposal> proposals);

    @Query("DELETE FROM proposal")
    void deleteProposals();
}
