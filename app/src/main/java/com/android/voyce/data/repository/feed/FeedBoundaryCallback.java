package com.android.voyce.data.repository.feed;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Executor;

public class FeedBoundaryCallback extends PagedList.BoundaryCallback<Post> {
    private FirebaseFirestore mDb;
    private UserPostDao mUserPostDao;
    private Executor mExecutor;
    private String mUid;

    public FeedBoundaryCallback(FirebaseFirestore db, UserPostDao dao, Executor executor, String uid) {
        mDb = db;
        mUserPostDao = dao;
        mExecutor = executor;
        mUid = uid;
    }

    @Override
    public void onZeroItemsLoaded() {
        Query reference = mDb.collection("feed")
                .document(mUid)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10);

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot results) {
                if (results != null && results.size() > 0) {
                    insertResults(results.toObjects(Post.class));
                }
            }
        });
    }

    @Override
    public void onItemAtEndLoaded(final @NonNull Post itemAtEnd) {
        Query reference = mDb.collection("feed")
                .document(mUid)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThan("timestamp", itemAtEnd.getTimestamp())
                .limit(10);

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot results) {
                if (results != null && results.size() > 0) {
                    insertResults(results.toObjects(Post.class));
                }
            }
        });
    }

    private void insertResults(final List<Post> posts) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mUserPostDao.insertPosts(posts);
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
