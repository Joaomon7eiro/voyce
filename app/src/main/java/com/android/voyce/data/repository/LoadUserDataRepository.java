package com.android.voyce.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.voyce.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadUserDataRepository {
    private static LoadUserDataRepository sInstance;

    public static LoadUserDataRepository getInstance() {
        if (sInstance == null) {
            sInstance = new LoadUserDataRepository();
        }
        return sInstance;
    }


    public LiveData<User> getUser(String userId) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    user.setId(documentSnapshot.getId());
                    userMutableLiveData.setValue(user);
                }
            }
        });

        return userMutableLiveData;
    }
}
