package com.android.voyce.data.repository.feed;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Executor;

public class RepoBoundaryCallback extends PagedList.BoundaryCallback<Post> {
    private FirebaseFirestore mDb;
    private UserPostDao mUserPostDao;
    private Executor mExecutor;
    private String mUid;
    private int mResultsCount;

    public RepoBoundaryCallback(FirebaseFirestore db, UserPostDao dao, Executor executor, String uid) {
        mDb = db;
        mUserPostDao = dao;
        mExecutor = executor;
        mUid = uid;
    }

    @Override
    public void onZeroItemsLoaded() {
        final List<String> followingIds = new ArrayList<>();
        CollectionReference reference = mDb.collection("user_following")
                .document(mUid).collection("users");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        followingIds.add(snapshot.getId());
                    }
                    if (followingIds.size() > 0) {
                        getPosts(followingIds);
                    }
                }
            }
        });
    }

    private void getPosts(final List<String> followingIds) {
        final List<Post> posts = new ArrayList<>();
        mResultsCount = 0;

        for (String id : followingIds) {
            mDb.collection("user_posts")
                    .document(id).collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3)
                    .get()
                    .addOnSuccessListener(
                            new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    mResultsCount += 1;
                                    if (queryDocumentSnapshots != null
                                            && queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Post post = snapshot.toObject(Post.class);
                                            post.setId(snapshot.getId());
                                            post.setCurrent_user_id(mUid);
                                            posts.add(post);
                                        }
                                    }
                                    if (mResultsCount == followingIds.size()) {
                                        insertResults(posts);
                                    }
                                }
                            });
        }
    }


    @Override
    public void onItemAtEndLoaded(final @NonNull Post itemAtEnd) {
        final List<PostIdAndTimestamp> idsTimestamps = new ArrayList<>();

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Post> posts = mUserPostDao.getPostsIdAndTimestamp(mUid);
                if (posts.size() > 0) {
                    String id = posts.get(0).getUser_id();
                    String startId = posts.get(0).getUser_id();

                    for (int i = 0; i < posts.size(); i++) {
                        if (!id.equals(posts.get(i).getUser_id())) {
                            id = posts.get(i).getUser_id();
                            idsTimestamps.add(new PostIdAndTimestamp(
                                    posts.get(i - 1).getUser_id(),
                                    posts.get(i - 1).getTimestamp()
                            ));
                        }
                        if (i + 1 == posts.size()) {
                            idsTimestamps.add(new PostIdAndTimestamp(
                                    posts.get(i).getUser_id(),
                                    posts.get(i).getTimestamp()
                            ));
                        }
                    }

                    if (id.equals(startId)) {
                        idsTimestamps.add(new PostIdAndTimestamp(
                                id,
                                posts.get(posts.size() - 1).getTimestamp()
                        ));
                    }
                }
                final List<String> followingIds = new ArrayList<>();

                CollectionReference reference = mDb.collection("user_following")
                        .document(mUid).collection("users");

                reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            followingIds.clear();
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                followingIds.add(snapshot.getId());
                            }
                            if (followingIds.size() > 0) {
                                getMorePosts(followingIds, idsTimestamps);
                            }
                        }
                    }
                });
            }
        });

    }

    private void getMorePosts(final List<String> followingIds, List<PostIdAndTimestamp> idTimestamps) {
        final List<Post> posts = new ArrayList<>();

        mResultsCount = 0;
        for (String id : followingIds) {
            long timestamp = System.currentTimeMillis();
            for (PostIdAndTimestamp postIdAndTimestamp : idTimestamps) {
                if (postIdAndTimestamp.getId().equals(id)) {
                    timestamp = postIdAndTimestamp.getTimestamp();
                }
            }

            mDb.collection("user_posts")
                    .document(id).collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .whereLessThan("timestamp", timestamp)
                    .limit(3)
                    .get()
                    .addOnSuccessListener(
                            new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    mResultsCount += 1;
                                    if (queryDocumentSnapshots != null
                                            && queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Post post = snapshot.toObject(Post.class);
                                            post.setId(snapshot.getId());
                                            post.setCurrent_user_id(mUid);
                                            posts.add(post);
                                        }
                                    }
                                    if (mResultsCount == followingIds.size() && posts.size() > 0) {
                                        insertResults(posts);
                                    }
                                }
                            });
        }
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

    private class PostIdAndTimestamp {
        private String id;
        private long timestamp;

        private PostIdAndTimestamp(String id, long timestamp) {
            this.id = id;
            this.timestamp = timestamp;
        }

        private String getId() {
            return id;
        }

        private long getTimestamp() {
            return timestamp;
        }
    }
}
