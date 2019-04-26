package com.android.voyce.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.MusiciansRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private LiveData<List<User>> mMusicians;
    private LiveData<Boolean> mIsLoading;

    public void init() {
        if (mMusicians != null) {
            return;
        }
        MusiciansRepository repository = MusiciansRepository.getInstance();
        mMusicians = repository.getMusicians();
        mIsLoading = repository.getIsLoading();
    }

    public LiveData<List<User>> getMusicians() {
        return mMusicians;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
