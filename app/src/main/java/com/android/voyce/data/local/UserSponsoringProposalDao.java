package com.android.voyce.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.UserSponsoringProposal;

import java.util.List;

@Dao
public interface UserSponsoringProposalDao {

    @Query("SELECT * from UserSponsoringProposal")
    List<UserSponsoringProposal> getProposals();

    @Insert
    void insertProposal(UserSponsoringProposal userSponsoringProposal);

    @Delete
    void deleteProposal(UserSponsoringProposal userSponsoringProposal);
}
