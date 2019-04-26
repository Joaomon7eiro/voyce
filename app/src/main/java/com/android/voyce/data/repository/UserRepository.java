package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class UserRepository {
    private static UserRepository sInstance;
    private static String mUserId;
    private final Executor mExecutor;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    private UserRepository(Executor executor) {
        mExecutor = executor;
    }

    public static UserRepository getInstance(String id) {
        if (sInstance == null) {
            sInstance = new UserRepository(AppExecutors.getInstance().getNetworkIO());
        }
        mUserId = id;
        return sInstance;
    }

    public LiveData<User> getUser() {
        final DocumentReference reference = mDb.collection("users").document(mUserId);
        final MutableLiveData<User> liveData = new MutableLiveData<>();

        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            User user = documentSnapshot.toObject(User.class);
                            liveData.postValue(user);
                        }
                        mIsLoading.postValue(false);
                    }
                });
            }
        });

        return liveData;
    }

    public LiveData<Goal> getGoalValue() {
        final DocumentReference reference = mDb.collection("goals").document(mUserId);
        final MutableLiveData<Goal> liveData = new MutableLiveData<>();

        mIsLoading.postValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            Goal goal = documentSnapshot.toObject(Goal.class);
                            liveData.postValue(goal);
                        }
                        mIsLoading.postValue(false);
                    }
                });
            }
        });
        return liveData;
    }

    public LiveData<List<Proposal>> getProposals() {
        final Query query = mDb.collection("proposals").whereEqualTo("user_id", mUserId);
        final MutableLiveData<List<Proposal>> liveData = new MutableLiveData<>();

        mIsLoading.postValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            List<Proposal> proposals = queryDocumentSnapshots.toObjects(Proposal.class);
                            if (proposals.size() != 0) {
                                liveData.postValue(proposals);
                            }
                        }
                        mIsLoading.postValue(false);
                    }
                });
            }
        });
        return liveData;
    }

    public LiveData<Boolean> getLoadingState() {
        return mIsLoading;
    }
}
