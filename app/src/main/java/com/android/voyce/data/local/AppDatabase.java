package com.android.voyce.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;

@Database(entities = {Musician.class, UserFollowingMusician.class, UserSponsoringProposal.class}, version = 1, exportSchema = false)
@TypeConverters(BitMapConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;
    private static final String DATABASE_NAME = "voyce";

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        .build();
            }
        }

        return sInstance;
    }

    public abstract UserFollowingMusicianDao userFollowingMusicianDao();

    public abstract MusicianDao musicianDao();

    public abstract UserSponsoringProposalDao userSponsoringProposalDao();
}
