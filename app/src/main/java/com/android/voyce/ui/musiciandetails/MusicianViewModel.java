package com.android.voyce.ui.musiciandetails;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.repository.MusicianRepository;

import java.util.List;

public class MusicianViewModel extends AndroidViewModel {

    private LiveData<User> mMusician;
    private LiveData<List<Proposal>> mProposals;
    private LiveData<Boolean> mIsLoading;
    private LiveData<Boolean> mIsFollowing;
    private MusicianRepository mRepository;

    public MusicianViewModel(@NonNull Application application) {
        super(application);
        mRepository = MusicianRepository.getInstance(application);
    }

    public void init(UserFollowingMusician userFollowingMusician) {
        if (mMusician != null) {
            return;
        }
        mRepository.setUserFollowingMusician(userFollowingMusician);
        mMusician = mRepository.getMusician();
        mProposals = mRepository.getProposals();
        mIsLoading = mRepository.getIsLoading();
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
