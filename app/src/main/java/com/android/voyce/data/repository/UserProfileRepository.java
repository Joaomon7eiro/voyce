package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;

import java.util.List;

public class UserProfileRepository {
    private static UserProfileRepository sInstance;
    private String mUserId;
    private final UserDao mUserDao;
    private final UserFollowingMusicianDao mUserFollowingMusicianDao;

    private UserProfileRepository(UserDao userDao, UserFollowingMusicianDao userFollowingMusicianDao) {
        mUserDao = userDao;
        mUserFollowingMusicianDao = userFollowingMusicianDao;
    }

    public static UserProfileRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new UserProfileRepository(
                    AppDatabase.getInstance(application).userDao(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao()
            );
        }
        return sInstance;
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    public LiveData<User> getUser() {
        return mUserDao.getUser(mUserId);
    }
    public LiveData<List<UserFollowingMusician>> getUserFollowingMusicians() {
        return mUserFollowingMusicianDao.queryMusiciansByUser(mUserId);
    }
}
