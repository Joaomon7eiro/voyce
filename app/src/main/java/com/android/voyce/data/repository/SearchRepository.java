package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.model.User;
import com.android.voyce.utils.AppExecutors;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executor;


public class SearchRepository {
    private static final String TAG = SearchRepository.class.getSimpleName();
    private static SearchRepository sInstance;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final UserDao mUserDao;
    private final Executor mExecutor;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private static FirebaseUser mCurrentUser;
    private String mUserCity;
    private String mUserState;

    private SearchRepository(UserDao userDao, Executor executor) {
        mUserDao = userDao;
        mExecutor = executor;
    }

    public static SearchRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new SearchRepository(AppDatabase.getInstance(application).userDao(),
                    AppExecutors.getInstance().getDiskIO());
        }
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        return sInstance;
    }

    public void refreshData(long refresh) {
        refreshUsers(refresh);
    }

    public LiveData<List<User>> getMusicians() {
        return mUserDao.getUsers(mCurrentUser.getUid());
    }

    public LiveData<List<User>> getCityMusicians() {
        return mUserDao.getUsersByCity(mCurrentUser.getUid(), mUserCity, mUserState.toUpperCase());
    }

    public LiveData<List<User>> getStateMusicians() {
        return mUserDao.getUsersByState(mCurrentUser.getUid(), mUserState.toUpperCase());
    }

    private void refreshUsers(final long refresh) {
        final long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int count = mUserDao.getUsersCount();
                boolean needsRefresh = (mUserDao.getUpdatedUsersCount(timestamp, refresh, mCurrentUser.getUid()) > 0);
                if (needsRefresh || count <= 1) {
                    mIsLoading.postValue(true);

                    Log.i(TAG, "Refreshing search users");

                    final Query query = mDb.collection("users")
                            .whereEqualTo("type", 1).limit(10);

                    final Query queryCity = mDb.collection("users")
                            .whereEqualTo("type", 1)
                            .whereEqualTo("city", mUserCity).limit(10);

                    final Query queryState = mDb.collection("users")
                            .whereEqualTo("type", 1)
                            .whereEqualTo("state", mUserState.toUpperCase()).limit(10);

                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserDao.deleteUsers(mCurrentUser.getUid());
                        }
                    });

                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots != null) {
                                final List<User> musicians = queryDocumentSnapshots.toObjects(User.class);
                                for (User user : musicians) {
                                    user.setLast_update_timestamp(timestamp);
                                }
                                mExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserDao.insertUsers(musicians);
                                        mIsLoading.postValue(false);
                                    }
                                });
                            }
                        }
                    });

                    queryCity.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots != null) {
                                final List<User> musicians = queryDocumentSnapshots.toObjects(User.class);
                                for (User user : musicians) {
                                    user.setLast_update_timestamp(timestamp);
                                }
                                mExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserDao.insertUsers(musicians);
                                        mIsLoading.postValue(false);
                                    }
                                });
                            }
                        }
                    });

                    queryState.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots != null) {
                                final List<User> musicians = queryDocumentSnapshots.toObjects(User.class);
                                for (User user : musicians) {
                                    user.setLast_update_timestamp(timestamp);
                                }
                                mExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserDao.insertUsers(musicians);
                                        mIsLoading.postValue(false);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void setCityAndState(String userCity, String userState) {
        mUserCity = userCity;
        mUserState = userState;
    }
}
