package com.android.voyce.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.voyce.data.model.Goal;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserGoalDao {

    @Query("SELECT * FROM goal where id = :id")
    LiveData<Goal> getGoal(String id);

    @Insert(onConflict = REPLACE)
    void insertGoal(Goal goal);
}
