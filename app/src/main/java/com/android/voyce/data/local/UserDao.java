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
    @Query("SELECT * FROM user where id = :id")
    LiveData<User> getUser(String id);

    @Query("SELECT * FROM user")
    LiveData<List<User>> getUsers();

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Insert(onConflict = REPLACE)
    void insertUsers(List<User> user);

    @Query("DELETE from user where id != :id")
    void deleteUsers(String id);

    @Query("SELECT COUNT(*) FROM user where :timestamp - last_update_timestamp > :refreshDelay")
    int getUpdatedUsersCount(long timestamp, long refreshDelay);

    @Query("SELECT COUNT(*) FROM user")
    int getUsersCount();
}
