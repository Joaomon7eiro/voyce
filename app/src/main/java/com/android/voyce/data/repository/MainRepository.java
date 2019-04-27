package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.local.UserGoalDao;
import com.android.voyce.data.local.UserProposalsDao;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class MainRepository {
    private static MainRepository sInstance;
    private String mUserId;
    private final Executor mExecutor;
    private final Executor mDiskExecutor;
    private final UserDao mUserDao;
    private final UserGoalDao mUserGoalDao;
    private final UserProposalsDao mUserProposalsDao;
    private final UserFollowingMusicianDao mUserFollowingMusiciansDao;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<User> mUserLiveData = new MutableLiveData<>();

    private MainRepository(Executor executor, Executor diskExecutor, UserDao userDao,
                           UserGoalDao userGoalDao, UserProposalsDao userProposalsDao,
                           UserFollowingMusicianDao userFollowingMusicianDao) {
        mExecutor = executor;
        mUserDao = userDao;
        mUserGoalDao = userGoalDao;
        mUserProposalsDao = userProposalsDao;
        mUserFollowingMusiciansDao = userFollowingMusicianDao;
        mDiskExecutor = diskExecutor;
    }

    public static MainRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MainRepository(
                    AppExecutors.getInstance().getNetworkIO(),
                    AppExecutors.getInstance().getDiskIO(),
                    AppDatabase.getInstance(application).userDao(),
                    AppDatabase.getInstance(application).userGoalDao(),
                    AppDatabase.getInstance(application).userProposalsDao(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao()
            );
        }
        return sInstance;
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    public void startUser() {
        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            final DocumentReference userReference = mDb.collection("users").document(mUserId);
            final DocumentReference goalReference = mDb.collection("goals").document(mUserId);
            final Query followersQuery = mDb.collection("musician_followers").whereEqualTo("follower_id", mUserId);
            final Query proposalsQuery = mDb.collection("proposals").whereEqualTo("user_id", mUserId);

            @Override
            public void run() {
                userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            final User user = documentSnapshot.toObject(User.class);
                            user.setId(documentSnapshot.getId());
                            mDiskExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mUserLiveData.postValue(user);
                                    mUserDao.insertUser(user);
                                    mIsLoading.postValue(false);
                                }
                            });
                        }
                    }
                });

                goalReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            final Goal goal = documentSnapshot.toObject(Goal.class);
                            goal.setId(documentSnapshot.getId());
                            mDiskExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mUserGoalDao.insertGoal(goal);
                                    mIsLoading.postValue(false);
                                }
                            });
                        }
                    }
                });

                proposalsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            final List<Proposal> proposalList = new ArrayList<>();
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                            for (int i = 0; i < documents.size(); i++) {
                                Proposal proposal = documents.get(i).toObject(Proposal.class);
                                proposal.setId(documents.get(i).getId());
                                proposalList.add(proposal);
                            }

                            mDiskExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mUserProposalsDao.insertProposals(proposalList);
                                    mIsLoading.postValue(false);
                                }
                            });
                        }
                    }
                });

                followersQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            final List<UserFollowingMusician> followingMusicians = new ArrayList<>();
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot doc: documents) {
                                UserFollowingMusician userFollowingMusician = doc.toObject(UserFollowingMusician.class);
                                userFollowingMusician.setId(doc.getId());
                                followingMusicians.add(userFollowingMusician);
                            }

                            mDiskExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mUserFollowingMusiciansDao.insertUserFollowingMusicians(followingMusicians);
                                    mIsLoading.postValue(false);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public LiveData<User> getUser() {
        return mUserLiveData;
    }
}
