package com.android.voyce.data.repository;

import com.android.voyce.data.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;

public class NewPostRepository {
    private static NewPostRepository sInstance;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    public static NewPostRepository getInstance() {
        if (sInstance == null) {
            sInstance = new NewPostRepository();
        }
        return sInstance;
    }

    public void createPost(Post post) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        post.setTimestamp(timestamp.getTime());
        mDb.collection("user_posts").document(post.getUser_id()).collection("posts")
                .add(post);
    }
}
