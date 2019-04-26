package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.Goal;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserGoalDao {

    @Query("SELECT * FROM goal where id = :id")
    LiveData<Goal> getGoal(String id);

    @Insert(onConflict = REPLACE)
    void insertGoal(Goal goal);
}
