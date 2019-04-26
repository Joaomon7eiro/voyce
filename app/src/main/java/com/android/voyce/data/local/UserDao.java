package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.User;


import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user where id = :id")
    LiveData<User> getUser(String id);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);
}
