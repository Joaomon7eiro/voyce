package com.android.voyce.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FeedRepository {
    private static FeedRepository sInstance;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private static FirebaseUser mUser;
    private MutableLiveData<List<Post>> mPostsLiveData = new MutableLiveData<>();

    public static FeedRepository getInstance() {
        if (sInstance == null) {
            sInstance = new FeedRepository();
        }
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        return sInstance;
    }

    public LiveData<List<Post>> getPosts() {
        final List<String> followingIds = new ArrayList<>();
        CollectionReference reference = mDb.collection("user_following").document(mUser.getUid()).collection("users");

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        followingIds.add(snapshot.getId());
                    }

                    final List<Post> posts = new ArrayList<>();
                    for (String id : followingIds) {
                        mDb.collection("user_posts").document(id).collection("posts")
                                .limit(3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        posts.add(snapshot.toObject(Post.class));
                                    }
                                    mPostsLiveData.setValue(posts);
                                }
                            }
                        });
                    }

                }
            }
        });


        return mPostsLiveData;
    }
}
