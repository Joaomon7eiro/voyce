package com.android.voyce.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.android.voyce.data.model.Song;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class UserSinglesDao {

    @Insert (onConflict = REPLACE)
    abstract void insert(List<Song> songList);

    @Query("SELECT * FROM song WHERE user_id = :userId")
    public abstract LiveData<List<Song>> getSingles(String userId);

    @Query("DELETE FROM song WHERE user_id = :userId")
    abstract void deleteAll(String userId);

    @Transaction
    public void updateData(List<Song> songs, String userId) {
        deleteAll(userId);
        insert(songs);
    }
}
