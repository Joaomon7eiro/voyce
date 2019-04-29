package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.local.UserGoalDao;
import com.android.voyce.data.local.UserProposalsDao;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;

import java.util.List;

public class UserRepository {
    private static UserRepository sInstance;
    private String mUserId;
    private final UserDao mUserDao;
    private final UserGoalDao mUserGoalDao;
    private final UserProposalsDao mUserProposalsDao;

    private UserRepository(UserDao userDao, UserGoalDao userGoalDao, UserProposalsDao userProposalsDao) {
        mUserDao = userDao;
        mUserGoalDao = userGoalDao;
        mUserProposalsDao = userProposalsDao;
    }

    public static UserRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new UserRepository(AppDatabase.getInstance(application).userDao(),
                    AppDatabase.getInstance(application).userGoalDao(),
                    AppDatabase.getInstance(application).userProposalsDao());
        }
        return sInstance;
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    public LiveData<User> getUser() {
        return mUserDao.getUser(mUserId);
    }

    public LiveData<Goal> getGoalValue() {
        return mUserGoalDao.getGoal(mUserId);
    }

    public LiveData<List<Proposal>> getProposals() {
        return mUserProposalsDao.getProposals(mUserId);
    }
}
