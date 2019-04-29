package com.android.voyce.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;

@Database(entities = {User.class, Goal.class, Proposal.class, UserFollowingMusician.class}, version = 1, exportSchema = false)
@TypeConverters(BitMapConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase sInstance;
    private static final String DATABASE_NAME = "voyce";

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        .build();

                Log.i(TAG, "Database created");
            }
        }

        return sInstance;
    }

    public abstract UserDao userDao();

    public abstract UserGoalDao userGoalDao();

    public abstract UserProposalsDao userProposalsDao();

    public abstract UserFollowingMusicianDao userFollowingMusicianDao();

}
