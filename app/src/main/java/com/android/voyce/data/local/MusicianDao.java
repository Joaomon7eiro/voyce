package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.Musician;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MusicianDao {
    @Query("SELECT * FROM musician order by name")
    LiveData<List<Musician>> getMusicians();

    @Insert(onConflict = REPLACE)
    void insertMusicians(List<Musician> musicians);

    @Query("SELECT COUNT(*) FROM musician where :timestamp - musician.last_update_timestamp > 10000")
    int getRefreshMusicians(long timestamp);
}
