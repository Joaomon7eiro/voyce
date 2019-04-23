package com.android.voyce.ui.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.repository.MusiciansRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private MutableLiveData<List<Musician>> mMusicians;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public void init() {
        if (mMusicians != null) {
            return;
        }
        MusiciansRepository repository = MusiciansRepository.getInstance();
        mMusicians = repository.getMusicians();
        mIsLoading = repository.getIsLoading();
    }

    public LiveData<List<Musician>> getMusicians() {
        return mMusicians;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
