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
    private LiveData<Boolean> mIsFollowing;
    private MusicianRepository mRepository;

    public void init(String id, String userId) {
        if (mMusician != null) {
            return;
        }
        mRepository = MusicianRepository.getInstance(id, userId);
        mMusician = mRepository.getMusician();
        mProposals = mRepository.getProposals();
        mIsLoading = mRepository.getIsLoading();
        mRepository.handleFollower();
        mIsFollowing = mRepository.getIsFollowing();
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

    public void handleFollower() {
        mRepository.handleFollower();
    }

    public LiveData<Boolean> getIsFollowing() {
        return mIsFollowing;
    }

}
