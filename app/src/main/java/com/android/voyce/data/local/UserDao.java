package com.android.voyce.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.voyce.data.model.User;


import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<User> getUser(String id);

    @Query("SELECT * FROM user WHERE id != :id AND type = 1")
    LiveData<List<User>> getUsers(String id);

    @Query("SELECT * FROM user WHERE id != :id AND type = 1 AND city = :city AND state = :state")
    LiveData<List<User>> getUsersByCity(String id, String city, String state);

    @Query("SELECT * FROM user WHERE id != :id AND type = 1 AND state = :state")
    LiveData<List<User>> getUsersByState(String id, String state);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Insert(onConflict = REPLACE)
    void insertUsers(List<User> user);

    @Query("DELETE from user WHERE id != :id")
    void deleteUsers(String id);
}
