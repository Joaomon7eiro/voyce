package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.android.voyce.data.model.UserSponsoringProposal;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class UserSponsoringDao {

    @Query("SELECT * FROM usersponsoringproposal where sponsor_id = :id")
    public abstract LiveData<List<UserSponsoringProposal>> queryProposalsByUser(String  id);

    @Insert(onConflict = REPLACE)
    public abstract void insertUserSponsoringProposals(List<UserSponsoringProposal> userSponsoringProposals);

    @Insert(onConflict = REPLACE)
    public abstract void insertUserSponsoringProposal(UserSponsoringProposal userSponsoringProposal);

    @Query("DELETE FROM usersponsoringproposal WHERE id = :id")
    public abstract void deleteById(String id);

    @Query("DELETE FROM usersponsoringproposal")
    public abstract void deleteAll();

    @Transaction
    public void updateData(List<UserSponsoringProposal> userSponsoringProposals){
        deleteAll();
        insertUserSponsoringProposals(userSponsoringProposals);
    }
}
