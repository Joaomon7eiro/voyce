package com.android.voyce.ui.musiciandetails;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.repository.MusicianRepository;

public class MusicianViewModel extends ViewModel {

    private MutableLiveData<Musician> mMusician;
    //private MutableLiveData<List<Proposal>> mProposals;
    private MutableLiveData<Proposal> mProposals;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public void init(String id) {
        if (mMusician != null) {
            return;
        }
        MusicianRepository repository = MusicianRepository.getInstance();
        mMusician = repository.getMusician(id);
        mProposals = repository.getProposals(id);
        mIsLoading = repository.getIsLoading();
    }

    public LiveData<Musician> getMusician() {
        return mMusician;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<Proposal> getProposals() {
        return mProposals;
    }

//    public LiveData<List<Proposal>> getProposals() {
//        return mProposals;
//    }
}
