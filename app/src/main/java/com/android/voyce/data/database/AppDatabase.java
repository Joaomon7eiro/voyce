package com.android.voyce.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.android.voyce.data.models.MusicianModel;
import com.android.voyce.data.models.ProposalModel;

@Database(entities = {MusicianModel.class, ProposalModel.class}, version = 1, exportSchema = false)
@TypeConverters(BitMapConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "voyce";

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }

        return sInstance;
    }

    public abstract MusicianDao musicianDao();

    public abstract ProposalDao proposalDao();
}
