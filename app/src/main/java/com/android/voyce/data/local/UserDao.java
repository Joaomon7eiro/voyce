package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.User;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<User> getUser(String id);

    @Query("SELECT * FROM user WHERE id != :id AND type = 1")
    LiveData<List<User>> getUsers(String id);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Insert(onConflict = REPLACE)
    void insertUsers(List<User> user);

    @Query("DELETE from user WHERE id != :id")
    void deleteUsers(String id);

    @Query("SELECT COUNT(*) FROM user WHERE id != :id AND :timestamp - last_update_timestamp > :refreshDelay")
    int getUpdatedUsersCount(long timestamp, long refreshDelay, String id);

    @Query("SELECT COUNT(*) FROM user")
    int getUsersCount();
}
