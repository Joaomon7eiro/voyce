package com.android.voyce.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.local.UserSponsoringDao;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserProfileRepository {
    private static UserProfileRepository sInstance;
    private final UserDao mUserDao;
    private final UserFollowingMusicianDao mUserFollowingMusicianDao;
    private final UserSponsoringDao mUserSponsoringDao;
    private static FirebaseUser mCurrentUser;

    private UserProfileRepository(UserDao userDao,
                                  UserFollowingMusicianDao userFollowingMusicianDao,
                                  UserSponsoringDao userSponsoringDao) {
        mUserDao = userDao;
        mUserFollowingMusicianDao = userFollowingMusicianDao;
        mUserSponsoringDao = userSponsoringDao;
    }

    public static UserProfileRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new UserProfileRepository(
                    AppDatabase.getInstance(application).userDao(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao(),
                    AppDatabase.getInstance(application).userSponsoringDao()
            );
        }
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        return sInstance;
    }

    public LiveData<User> getUser() {
        return mUserDao.getUser(mCurrentUser.getUid());
    }

    public LiveData<List<UserFollowingMusician>> getUserFollowingMusicians() {
        return mUserFollowingMusicianDao.queryMusiciansByUser(mCurrentUser.getUid());
    }

    public LiveData<List<UserSponsoringProposal>> getUserSponsoringProposals() {
        return mUserSponsoringDao.queryProposalsByUser(mCurrentUser.getUid());
    }
}
