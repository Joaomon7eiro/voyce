package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserSponsoringDao {

    @Query("SELECT * FROM usersponsoringproposal where sponsor_id = :id")
    LiveData<List<UserFollowingMusician>> queryProposalsByUser(String  id);

    @Insert(onConflict = REPLACE)
    void insertUserSponsoringProposals(List<UserSponsoringProposal> userSponsoringProposals);

    @Insert(onConflict = REPLACE)
    void insertUserSponsoringProposal(UserSponsoringProposal userSponsoringProposal);

    @Query("DELETE FROM usersponsoringproposal WHERE id = :id")
    void deleteById(String id);

}
