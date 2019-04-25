package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.UserFollowingMusician;

import java.util.List;

@Dao
public interface UserFollowingMusicianDao {

    @Query("SELECT * FROM UserFollowingMusician ORDER BY name")
    LiveData<List<UserFollowingMusician>> queryMusicians();

    @Query("SELECT * FROM UserFollowingMusician WHERE id = :id")
    LiveData<UserFollowingMusician> queryMusiciansById(String id);

    @Insert
    void insertMusician(UserFollowingMusician userFollowingMusician);

    @Delete
    void deleteMusician(UserFollowingMusician userFollowingMusician);
}
