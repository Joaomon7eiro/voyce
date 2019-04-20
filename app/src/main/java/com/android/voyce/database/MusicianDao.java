package com.android.voyce.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MusicianDao {

    @Query("SELECT * FROM musicians ORDER BY name")
    LiveData<List<MusicianModel>> queryMusicians();

    @Query("SELECT * FROM musicians WHERE id = :id")
    LiveData<MusicianModel> queryMusiciansById(String id);

    @Insert
    void insertMusician(MusicianModel musicianModel);

    @Delete
    void deleteMusician(MusicianModel musicianModel);
}
