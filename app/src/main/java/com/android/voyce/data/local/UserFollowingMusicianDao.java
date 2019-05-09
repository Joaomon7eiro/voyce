package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.android.voyce.data.model.UserFollowingMusician;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class UserFollowingMusicianDao {

    @Query("SELECT * FROM UserFollowingMusician where follower_id = :id")
    public abstract LiveData<List<UserFollowingMusician>> queryMusiciansByUser(String  id);

    @Insert(onConflict = REPLACE)
    public abstract void insertUserFollowingMusicians(List<UserFollowingMusician> userFollowingMusician);

    @Insert(onConflict = REPLACE)
    public abstract void insertUserFollowingMusician(UserFollowingMusician userFollowingMusician);

    @Query("DELETE FROM UserFollowingMusician WHERE id = :id")
    public abstract void deleteById(String id);

    @Query("DELETE FROM UserFollowingMusician")
    public abstract void deleteAll();

    @Transaction
    public void updateData(List<UserFollowingMusician> userFollowingMusician) {
        deleteAll();
        insertUserFollowingMusicians(userFollowingMusician);
    }
}
