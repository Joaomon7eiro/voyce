package com.android.voyce.ui.usermusicianprofile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.voyce.data.model.User;
import com.android.voyce.data.remote.FirebaseDocumentLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class UserMusicianProfileViewModel extends ViewModel {
    private DocumentReference mUserReference;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private String mUserId;

    public void init(String userId) {
        mUserId = userId;
        mUserReference = FirebaseFirestore.getInstance().collection("users").document(mUserId);
        mUserReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                User user = documentSnapshot.toObject(User.class);
                userLiveData.setValue(user);
            }
        });
    }

    @NonNull
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
}
