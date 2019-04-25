package com.android.voyce.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.repository.MusiciansRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private LiveData<List<Musician>> mMusicians;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MusiciansRepository mRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository = MusiciansRepository.getInstance(application);
    }

    public void init() {
        if (mMusicians != null) {
            return;
        }
        mMusicians = mRepository.getMusicians();
        mIsLoading = mRepository.getIsLoading();
    }

    public LiveData<List<Musician>> getMusicians() {
        return mMusicians;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
