package com.android.voyce.ui.feed;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.android.voyce.data.model.Post;
import com.android.voyce.data.repository.feed.FeedRepository;

import java.util.concurrent.TimeUnit;

public class FeedViewModel extends AndroidViewModel {
    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(30);
    private FeedRepository mRepository;
    private LiveData<PagedList<Post>> mPostLiveData;
    private LiveData<Boolean> mIsLoading;

    FeedViewModel(@NonNull Application application) {
        super(application);
        mRepository = FeedRepository.getInstance(application);
        mRepository.refreshData(REFRESH_DELAY);
        mPostLiveData = mRepository.getPosts();
        mIsLoading = mRepository.getIsLoading();
    }

    public LiveData<PagedList<Post>> getPostLiveData() {
        return mPostLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void refreshData(long refresh) {
        mRepository.refreshData(refresh);
    }
}
