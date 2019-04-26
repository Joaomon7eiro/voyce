package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.model.User;

public class UserProfileRepository {
    private static UserProfileRepository sInstance;
    private String mUserId;
    private final UserDao mUserDao;

    private UserProfileRepository(UserDao userDao) {
        mUserDao = userDao;
    }

    public static UserProfileRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new UserProfileRepository(AppDatabase.getInstance(application).userDao());
        }
        return sInstance;
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    public LiveData<User> getUser() {
        return mUserDao.getUser(mUserId);
    }

}
