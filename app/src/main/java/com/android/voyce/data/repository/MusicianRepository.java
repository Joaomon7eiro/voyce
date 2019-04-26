package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class MusicianRepository {
    private static MusicianRepository sInstance;
    private static String mMusicianId;
    private static String mUserId;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final Executor mExecutor;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private boolean mIsFollowing;
    private String mMusicianFollowerId;

    private MusicianRepository(Executor executor) {
        mExecutor = executor;
    }

    public static MusicianRepository getInstance(String musicianId, String userId) {
        if (sInstance == null) {
            sInstance = new MusicianRepository(AppExecutors.getInstance().getNetworkIO());
        }
        mMusicianId = musicianId;
        mUserId = userId;
        return sInstance;
    }

    public LiveData<User> getMusician() {
        final DocumentReference reference = mDb.collection("users").document(mMusicianId);
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
        final Query query = mDb.collection("proposals").whereEqualTo("user_id", mMusicianId);
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

        if (!mIsFollowing) {
            Map<String, Object> followers = new HashMap<>();
            followers.put("follower_id", mUserId);
            followers.put("musician_id", mMusicianId);

            Task<DocumentReference> data = mDb.collection("musician_followers").add(followers);
            data.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    mMusicianFollowerId = documentReference.getId();
                }
            });
        } else {
            mDb.collection("musician_followers").document(mMusicianFollowerId).delete();
        }

    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<Boolean> getIsFollowing() {
        final MutableLiveData<Boolean> isFollowing = new MutableLiveData<>();

        final Query query = mDb.collection("musician_followers")
                .whereEqualTo("musician_id", mMusicianId).whereEqualTo("follower_id", mUserId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.size() > 0) {
                        List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                        mMusicianFollowerId = data.get(0).getId();
                        isFollowing.setValue(true);
                        mIsFollowing = true;
                    } else {
                        isFollowing.setValue(false);
                        mIsFollowing = false;
                    }
                }
            }
        });
        return isFollowing;
    }

}
