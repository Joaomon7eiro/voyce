package com.android.voyce.ui.feed;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.Post;

import java.util.List;

public class FeedViewModel extends ViewModel {
    private FeedRepository mRepository;
    private LiveData<List<Post>> mPostliveData;
    public void init() {
        mRepository = FeedRepository.getInstance();
        mPostliveData = mRepository.getPosts();
    }

    public LiveData<List<Post>> getPostliveData() {
        return mPostliveData;
    }
}
