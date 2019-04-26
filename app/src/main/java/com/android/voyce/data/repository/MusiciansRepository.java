package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.Executor;


public class MusiciansRepository {
    private static MusiciansRepository sInstance;
    private final Executor mExecutor;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    private MusiciansRepository(Executor executor) {
        mExecutor = executor;
    }
    public static MusiciansRepository getInstance() {
        if (sInstance == null) {
            sInstance = new MusiciansRepository(AppExecutors.getInstance().getNetworkIO());
        }
        return sInstance;
    }

    public LiveData<List<User>> getMusicians() {
        final Query query = mDb.collection("users").whereEqualTo("type",  1);
        final MutableLiveData<List<User>> liveData = new MutableLiveData<>();

        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            List<User> musicians = queryDocumentSnapshots.toObjects(User.class);
                            liveData.setValue(musicians);
                        }
                        mIsLoading.setValue(false);
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
