package com.android.voyce.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.voyce.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
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

            Query query = mDb.collection("users")
                    .orderBy("name")
                    .whereEqualTo("type", 1)
                    .startAt(queryFormatted)
                    .endAt(queryFormatted + '\uf8ff');

            mIsLoading.setValue(true);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        List<User> usersList = queryDocumentSnapshots.toObjects(User.class);
                        mUsers.postValue(usersList);
                    }
                    mIsLoading.setValue(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mIsLoading.setValue(false);
                    e.printStackTrace();
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
