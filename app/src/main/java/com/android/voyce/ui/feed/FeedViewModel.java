package com.android.voyce.ui.feed;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.android.voyce.data.model.Post;
import com.android.voyce.data.repository.FeedRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FeedViewModel extends AndroidViewModel {
    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(30);
    private FeedRepository mRepository;
    private LiveData<List<Post>> mPostLiveData;
    private LiveData<Boolean> mIsLoading;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        mRepository = FeedRepository.getInstance(application);
    }

    public void init(Boolean firstTimeCreated) {
        if (firstTimeCreated != null) {
            mRepository.refreshData(REFRESH_DELAY, true);
        } else {
            mRepository.refreshData(REFRESH_DELAY, false);
        }
        mPostLiveData = mRepository.getPosts();
        mIsLoading = mRepository.getIsLoading();
    }

    public LiveData<List<Post>> getPostLiveData() {
        return mPostLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void refreshData(long refresh) {
        mRepository.refreshData(refresh, false);
    }
}
