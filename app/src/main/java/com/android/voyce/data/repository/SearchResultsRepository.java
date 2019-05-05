package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SearchResultsRepository {
    private static SearchResultsRepository sInstance;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<User>> mUsers = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public SearchResultsRepository() {
    }

    public static SearchResultsRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SearchResultsRepository();
        }

        return sInstance;
    }

    public LiveData<List<User>> getUsers(String queryString) {
        if (queryString != null) {
            String queryFormatted = queryString.toLowerCase().trim();

            Query query = mDb.collection("users").orderBy("name").startAt(queryFormatted).endAt(queryFormatted + '\uf8ff');

            mIsLoading.setValue(true);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        List<User> usersList = queryDocumentSnapshots.toObjects(User.class);
                        mUsers.postValue(usersList);
                        mIsLoading.setValue(false);
                    }
                }
            });
        } else {
            mIsLoading.setValue(false);
        }
        return mUsers;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
