package com.android.voyce.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SearchRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private LiveData<List<User>> mMusicians;
    private LiveData<Boolean> mIsLoading;
    private SearchRepository mRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository = SearchRepository.getInstance(application);
    }

    public void init() {
        if (mMusicians != null) {
            return;
        }
        mMusicians = mRepository.getMusicians();
        mIsLoading = mRepository.getIsLoading();
    }

    public LiveData<List<User>> getMusicians() {
        return mMusicians;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
