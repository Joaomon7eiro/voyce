package com.android.voyce.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;

@Database(entities = {User.class, Post.class, Goal.class, Proposal.class, UserFollowingMusician.class, UserSponsoringProposal.class}, version = 1, exportSchema = false)
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

    public abstract UserPostDao userPostDao();

    public abstract UserGoalDao userGoalDao();

    public abstract UserProposalsDao userProposalsDao();

    public abstract UserFollowingMusicianDao userFollowingMusicianDao();

    public abstract UserSponsoringDao userSponsoringDao();

}
