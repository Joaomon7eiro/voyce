package com.android.voyce.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.local.UserSinglesDao;
import com.android.voyce.data.local.UserSponsoringDao;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.Song;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;
import com.android.voyce.utils.AppExecutors;
import com.android.voyce.data.local.UserDao;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.local.UserGoalDao;
import com.android.voyce.data.local.UserProposalsDao;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class MainRepository {
    private static MainRepository sInstance;
    private String mUserId;
    private final Executor mDiskExecutor;
    private final UserDao mUserDao;
    private final UserGoalDao mUserGoalDao;
    private final UserSinglesDao mUserSinglesDao;
    private final UserProposalsDao mUserProposalsDao;
    private final UserFollowingMusicianDao mUserFollowingMusiciansDao;
    private final UserSponsoringDao mUserSponsoringDao;
    private final UserPostDao mUserPostDao;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<User> mUserLiveData = new MutableLiveData<>();

    private MainRepository(Executor diskExecutor, UserDao userDao,
                           UserGoalDao userGoalDao, UserProposalsDao userProposalsDao,
                           UserFollowingMusicianDao userFollowingMusicianDao,
                           UserSponsoringDao userSponsoringDao,
                           UserPostDao userPostDao,
                           UserSinglesDao userSinglesDao
    ) {
        mUserDao = userDao;
        mUserGoalDao = userGoalDao;
        mUserProposalsDao = userProposalsDao;
        mUserFollowingMusiciansDao = userFollowingMusicianDao;
        mDiskExecutor = diskExecutor;
        mUserSponsoringDao = userSponsoringDao;
        mUserPostDao = userPostDao;
        mUserSinglesDao = userSinglesDao;
    }

    public static MainRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MainRepository(
                    AppExecutors.getInstance().getDiskIO(),
                    AppDatabase.getInstance(application).userDao(),
                    AppDatabase.getInstance(application).userGoalDao(),
                    AppDatabase.getInstance(application).userProposalsDao(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao(),
                    AppDatabase.getInstance(application).userSponsoringDao(),
                    AppDatabase.getInstance(application).userPostDao(),
                    AppDatabase.getInstance(application).userSinglesDao()
            );
        }
        return sInstance;
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    public void startUser() {
        mIsLoading.setValue(true);

        CollectionReference userCollectionReference = mDb.collection("users");
        final DocumentReference userReference = userCollectionReference.document(mUserId);
        CollectionReference proposalsCollectionReference = userReference.collection("proposals");
        CollectionReference followingReference = mDb.collection("user_following").document(mUserId).collection("users");
        CollectionReference followersReference = mDb.collection("user_followers").document(mUserId).collection("users");
        CollectionReference sponsorsReference = mDb.collection("user_sponsors").document(mUserId).collection("users");
        CollectionReference sponsoringReference = mDb.collection("user_sponsoring").document(mUserId).collection("sponsoring");
        CollectionReference userFeedReference = mDb.collection("feed").document(mUserId).collection("posts");
        Query singlesReference = mDb.collection("music").document(mUserId).collection("singles").orderBy("position", Query.Direction.DESCENDING);

        proposalsCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    final List<Proposal> proposals = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Proposal proposal = documentSnapshot.toObject(Proposal.class);
                        proposal.setId(documentSnapshot.getId());
                        proposal.setUser_id(mUserId);
                        proposals.add(proposal);
                    }

                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserProposalsDao.deleteProposals();
                            mUserProposalsDao.insertProposals(proposals);
                            mIsLoading.postValue(false);
                        }
                    });
                }
            }
        });

        singlesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    final List<Song> songList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Song song = documentSnapshot.toObject(Song.class);
                        song.setId(documentSnapshot.getId());
                        song.setUser_id(mUserId);
                        songList.add(song);
                    }

                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserSinglesDao.updateData(songList, mUserId);
                            mIsLoading.postValue(false);
                        }
                    });
                }
            }
        });

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    final User user = documentSnapshot.toObject(User.class);
                    final Goal goal = hashToGoalObject(documentSnapshot.get("goal"));

                    user.setId(documentSnapshot.getId());

                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserLiveData.postValue(user);
                            mUserDao.insertUser(user);
                            if (goal != null) {
                                goal.setId(documentSnapshot.getId());
                                mUserGoalDao.insertGoal(goal);
                            }
                            mIsLoading.postValue(false);
                        }
                    });
                }
            }
        });

        followingReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    final List<UserFollowingMusician> userFollowingMusicians = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserFollowingMusician userFollowingMusician = documentSnapshot.toObject(UserFollowingMusician.class);
                        userFollowingMusician.setFollower_id(mUserId);
                        userFollowingMusician.setId(documentSnapshot.getId());
                        userFollowingMusicians.add(userFollowingMusician);
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("following", userFollowingMusicians.size());
                    userReference.update(map);

                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserFollowingMusiciansDao.updateData(userFollowingMusicians);
                            mIsLoading.postValue(false);
                        }
                    });
                }
            }
        });

        sponsoringReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    final List<UserSponsoringProposal> userSponsoringProposals = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserSponsoringProposal userSponsoringProposal = documentSnapshot.toObject(UserSponsoringProposal.class);
                        userSponsoringProposal.setSponsor_id(mUserId);
                        userSponsoringProposal.setId(documentSnapshot.getId());
                        userSponsoringProposals.add(userSponsoringProposal);
                    }

                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserSponsoringDao.updateData(userSponsoringProposals);
                            mIsLoading.postValue(false);
                        }
                    });
                }
            }
        });

        followersReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("followers", queryDocumentSnapshots.size());
                    userReference.update(map);
                }
            }
        });

        sponsorsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sponsors", queryDocumentSnapshots.size());
                    userReference.update(map);
                }
            }
        });

        userFeedReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot results, @Nullable FirebaseFirestoreException e) {
                if (results != null) {
                    final List<Post> posts = results.toObjects(Post.class);

                    for (Post post : posts) {
                        post.setCurrent_user_id(mUserId);
                    }
                    mDiskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mUserPostDao.updateData(posts, mUserId);
                        }
                    });
                }
            }
        });
    }

    private Goal hashToGoalObject(Object hashMap) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(hashMap);
        return gson.fromJson(jsonElement, Goal.class);
    }

    public void setSignalId(String id) {
        CollectionReference userCollectionReference = mDb.collection("users");
        DocumentReference userReference = userCollectionReference.document(mUserId);

        Map<String, Object> updateSignalId = new HashMap<>();
        updateSignalId.put("signal_id", id);
        userReference.update(updateSignalId);
    }

    public LiveData<User> getUser() {
        return mUserLiveData;
    }
}
