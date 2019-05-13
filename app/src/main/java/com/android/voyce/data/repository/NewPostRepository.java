package com.android.voyce.data.repository;

import com.android.voyce.data.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void createPost(final Post post) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        post.setTimestamp(timestamp.getTime());
        mDb.collection("user_posts").document(post.getUser_id())
                .collection("posts")
                .add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if (documentReference != null) {
                    post.setId(documentReference.getId());
                    postOnFollowersFeed(post);
                }
            }
        });
    }

    private void postOnFollowersFeed(final Post post) {
        mDb.collection("user_followers").document(post.getUser_id())
                .collection("users").get().addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        mDb.collection("feed").document(document.getId())
                                .collection("posts").document(post.getId()).set(post);
                    }
                }
            }
        });
    }
}
