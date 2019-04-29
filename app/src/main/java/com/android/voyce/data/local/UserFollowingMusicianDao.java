package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.UserFollowingMusician;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserFollowingMusicianDao {

    @Query("SELECT * FROM UserFollowingMusician where follower_id = :id")
    LiveData<List<UserFollowingMusician>> queryMusiciansByUser(String  id);

    @Insert(onConflict = REPLACE)
    void insertUserFollowingMusicians(List<UserFollowingMusician> userFollowingMusician);

    @Insert(onConflict = REPLACE)
    void insertUserFollowingMusician(UserFollowingMusician userFollowingMusician);

    @Query("DELETE FROM UserFollowingMusician WHERE id = :id")
    void deleteById(String id);
}
