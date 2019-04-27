package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.List;
import java.util.concurrent.Executor;


public class MusicianRepository {
    private static MusicianRepository sInstance;
    private UserFollowingMusician mUserFollowingMusician;

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final Executor mExecutor;
    private final Executor mDiskExecutor;
    private final UserFollowingMusicianDao mUserFollowingMusicianDao;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    final MutableLiveData<Boolean> mIsFollowing = new MutableLiveData<>();

    private boolean mBolIsFollowing;
    private String mMusicianFollowerId;

    private MusicianRepository(Executor executor, Executor diskExecutor, UserFollowingMusicianDao userFollowingMusicianDao) {
        mExecutor = executor;
        mDiskExecutor = diskExecutor;
        mUserFollowingMusicianDao = userFollowingMusicianDao;
    }

    public static MusicianRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MusicianRepository(
                    AppExecutors.getInstance().getNetworkIO(),
                    AppExecutors.getInstance().getDiskIO(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao());
        }
        return sInstance;
    }

    public void setUserFollowingMusician(UserFollowingMusician userFollowingMusician) {
        mUserFollowingMusician = userFollowingMusician;
    }


    public LiveData<User> getMusician() {
        final DocumentReference reference = mDb.collection("users").document(mUserFollowingMusician.getMusician_id());
        final MutableLiveData<User> liveData = new MutableLiveData<>();

        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            User musician = documentSnapshot.toObject(User.class);
                            liveData.postValue(musician);
                        }
                        mIsLoading.postValue(false);
                    }
                });
            }
        });
        return liveData;
    }

    public LiveData<List<Proposal>> getProposals() {
        final Query query = mDb.collection("proposals").whereEqualTo("user_id", mUserFollowingMusician.getMusician_id());
        final MutableLiveData<List<Proposal>> liveData = new MutableLiveData<>();

        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            List<Proposal> proposals = queryDocumentSnapshots.toObjects(Proposal.class);
                            liveData.postValue(proposals);
                        }
                        mIsLoading.postValue(false);
                    }
                });
            }
        });
        return liveData;
    }

    public void handleFollower() {

        if (!mBolIsFollowing) {
            Task<DocumentReference> data = mDb.collection("musician_followers").add(mUserFollowingMusician);
            data.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    mMusicianFollowerId = documentReference.getId();
                }
            });
            mIsFollowing.setValue(true);
            mBolIsFollowing = true;
        } else {
            mDb.collection("musician_followers").document(mMusicianFollowerId).delete();
            mDiskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserFollowingMusicianDao.deleteById(mMusicianFollowerId);
                }
            });
            mIsFollowing.setValue(false);
            mBolIsFollowing = false;
        }
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<Boolean> getIsFollowing() {

        final Query query = mDb.collection("musician_followers")
                .whereEqualTo("musician_id", mUserFollowingMusician.getMusician_id())
                .whereEqualTo("follower_id", mUserFollowingMusician.getFollower_id());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.size() > 0) {
                        List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                        mMusicianFollowerId = data.get(0).getId();
                        mIsFollowing.setValue(true);
                        mBolIsFollowing = true;
                    } else {
                        mIsFollowing.setValue(false);
                        mBolIsFollowing = false;
                    }
                }
            }
        });
        return mIsFollowing;
    }

}
