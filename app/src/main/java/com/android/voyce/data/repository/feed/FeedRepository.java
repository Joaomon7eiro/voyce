package com.android.voyce.data.repository.feed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.model.Post;
import com.android.voyce.utils.AppExecutors;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Executor;


public class FeedRepository {
    private static FeedRepository sInstance;
    private final UserPostDao mUserPostDao;
    private final Executor mExecutor;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private static FirebaseUser mCurrentUser;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private long mLastRefreshTime = System.currentTimeMillis();
    private int mResultsCount;

    public FeedRepository(UserPostDao userPostDao, Executor executor) {
        mUserPostDao = userPostDao;
        mExecutor = executor;
    }

    public static FeedRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new FeedRepository(
                    AppDatabase.getInstance(application).userPostDao(),
                    AppExecutors.getInstance().getDiskIO());
        }
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        return sInstance;
    }

    public LiveData<PagedList<Post>> getPosts() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(15)
                .setPrefetchDistance(0)
                .build();

        DataSource.Factory factory = mUserPostDao.getPosts(mCurrentUser.getUid());
        RepoBoundaryCallback boundaryCallback = new RepoBoundaryCallback(mDb, mUserPostDao, mExecutor, mCurrentUser.getUid());
        return new LivePagedListBuilder<>(factory, config).setBoundaryCallback(boundaryCallback).build();
    }

    public void refreshData(final long refreshDelay, final boolean isFirstRefresh) {
        long currentTimeInMillis = System.currentTimeMillis();

        if (currentTimeInMillis - mLastRefreshTime > refreshDelay || isFirstRefresh) {
            mLastRefreshTime = currentTimeInMillis;
            mIsLoading.setValue(true);
            final List<String> followingIds = new ArrayList<>();
            CollectionReference reference = mDb.collection("user_following")
                    .document(mCurrentUser.getUid()).collection("users");

            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        followingIds.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            followingIds.add(snapshot.getId());
                        }
                        getFollowingUsersPosts(followingIds);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mIsLoading.setValue(false);
                }
            });
        }

    }

    private void getFollowingUsersPosts(List<String> followingIds) {
        final int length = followingIds.size();
        if (length == 0) {
            mIsLoading.setValue(false);
            return;
        }
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
                                            post.setCurrent_user_id(mCurrentUser.getUid());
                                            posts.add(post);
                                        }
                                    }
                                    if (mResultsCount == length) {
                                        insertResults(posts);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mIsLoading.setValue(false);
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
                mIsLoading.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
