package com.android.voyce.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SearchRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private static final long REFRESH_DELAY = 3600000; // 1 hour in milliseconds
    private LiveData<List<User>> mMusicians;
    private LiveData<List<User>> mCityMusicians;
    private LiveData<List<User>> mStateMusicians;
    private LiveData<Boolean> mIsLoading;
    private SearchRepository mRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository = SearchRepository.getInstance(application);
    }

    public void init(String userCity, String userState) {
        if (mMusicians != null) {
            return;
        }
        mRepository.setCityAndState(userCity, userState);
        mRepository.refreshData(REFRESH_DELAY);
        mMusicians = mRepository.getMusicians();
        mCityMusicians = mRepository.getCityMusicians();
        mStateMusicians = mRepository.getStateMusicians();
        mIsLoading = mRepository.getIsLoading();
    }

    public LiveData<List<User>> getMusicians() {
        return mMusicians;
    }

    public LiveData<List<User>> getCityMusicians() {
        return mCityMusicians;
    }

    public LiveData<List<User>> getStateMusicians() {
        return mStateMusicians;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void refreshData(long refresh) {
        mRepository.refreshData(refresh);
        mRepository.getMusicians();
        mRepository.getCityMusicians();
        mRepository.getStateMusicians();
    }
}
