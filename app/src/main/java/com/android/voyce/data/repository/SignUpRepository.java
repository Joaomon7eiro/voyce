package com.android.voyce.data.repository;

import com.android.voyce.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpRepository {
    private static SignUpRepository sInstance;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public static SignUpRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SignUpRepository();
        }
        return sInstance;
    }

    public void registerUser(User user) {
        mFirestore.collection("users").document(user.getId()).set(user);
    }
}
