package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.model.Post;
import com.android.voyce.utils.AppExecutors;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class FeedRepository {
    private static FeedRepository sInstance;
    private final UserPostDao mUserPostDao;
    private final Executor mExecutor;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private static FirebaseUser mCurrentUser;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public FeedRepository(UserPostDao userPostDao, Executor executor) {
        mUserPostDao = userPostDao;
        mExecutor = executor;
    }

    public static FeedRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new FeedRepository(AppDatabase.getInstance(application).userPostDao(), AppExecutors.getInstance().getDiskIO());
        }
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        return sInstance;
    }

    public LiveData<List<Post>> getPosts() {
        return mUserPostDao.queryPosts(mCurrentUser.getUid());
    }

    public void refreshData(final long refreshDelay, final boolean isFirstRefresh) {
        final long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean needsRefresh = (mUserPostDao.getUpdatedPostsCount(timestamp,
                        refreshDelay, mCurrentUser.getUid()) > 0);
                if (needsRefresh || isFirstRefresh) {
                    mIsLoading.postValue(true);
                    final List<String> followingIds = new ArrayList<>();

                    CollectionReference reference = mDb.collection("user_following")
                            .document(mCurrentUser.getUid()).collection("users");
                    reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    followingIds.add(snapshot.getId());
                                }
                                getFollowingUsersPosts(followingIds, timestamp);
                            }
                        }
                    });
                }
            }
        });
    }

    private void getFollowingUsersPosts(List<String> followingIds, final long timestamp) {
        final List<Post> posts = new ArrayList<>();
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
                                    if (queryDocumentSnapshots != null
                                            && queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Post post = snapshot.toObject(Post.class);
                                            post.setId(snapshot.getId());
                                            post.setCurrent_user_id(mCurrentUser.getUid());
                                            post.setLast_update_timestamp(timestamp);
                                            posts.add(post);
                                        }
                                    }
                                    mExecutor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mUserPostDao.insertPosts(posts);
                                            mIsLoading.postValue(false);
                                        }
                                    });
                                }
                            });
        }
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
