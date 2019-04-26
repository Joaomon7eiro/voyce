package com.android.voyce.ui.musiciandetails;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.MusicianRepository;

import java.util.List;

public class MusicianViewModel extends ViewModel {

    private LiveData<User> mMusician;
    private LiveData<List<Proposal>> mProposals;
    private LiveData<Boolean> mIsLoading;

    public void init(String id) {
        if (mMusician != null) {
            return;
        }
        MusicianRepository repository = MusicianRepository.getInstance(id);
        mMusician = repository.getMusician();
        mProposals = repository.getProposals();
        mIsLoading = repository.getIsLoading();
    }

    public LiveData<User> getMusician() {
        return mMusician;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<List<Proposal>> getProposals() {
        return mProposals;
    }
}
