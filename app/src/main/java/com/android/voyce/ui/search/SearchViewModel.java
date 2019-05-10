package com.android.voyce.ui.search;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SearchRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchViewModel extends AndroidViewModel {
    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(60);
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
    }
}
