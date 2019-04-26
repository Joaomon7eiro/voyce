package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.remote.ApiWebService;
import com.android.voyce.data.remote.WebService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MusicianRepository {
    private static MusicianRepository sInstance;
    private static String mUserId;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final Executor mExecutor;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    private MusicianRepository(Executor executor) {
        mExecutor = executor;
    }

    public static MusicianRepository getInstance(String id) {
        if (sInstance == null) {
            sInstance = new MusicianRepository(AppExecutors.getInstance().getNetworkIO());
        }
        mUserId = id;
        return sInstance;
    }

    public LiveData<User> getMusician() {
        final DocumentReference reference = mDb.collection("users").document(mUserId);
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
        final Query query = mDb.collection("proposals").whereEqualTo("user_id", mUserId);
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

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
