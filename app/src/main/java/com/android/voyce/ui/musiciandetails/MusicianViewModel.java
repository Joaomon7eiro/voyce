package com.android.voyce.ui.musiciandetails;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.repository.MusicianDetailsRepository;

import java.util.List;

public class MusicianViewModel extends AndroidViewModel {

    private LiveData<User> mMusician;
    private LiveData<List<Proposal>> mProposals;
    private LiveData<Boolean> mIsLoading;
    private LiveData<Boolean> mIsFollowing;
    private LiveData<Goal> mGoal;
    private MusicianDetailsRepository mRepository;

    public MusicianViewModel(@NonNull Application application) {
        super(application);
        mRepository = MusicianDetailsRepository.getInstance(application);
    }

    public void init(UserFollowingMusician userFollowingMusician) {
        if (mMusician != null) {
            return;
        }
        mRepository.setUserFollowingMusician(userFollowingMusician);
        mMusician = mRepository.getMusician();
        mProposals = mRepository.getProposals();
        mGoal = mRepository.getGoal();
        mIsFollowing = mRepository.getIsFollowing();
        mIsLoading = mRepository.getIsLoading();
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

    public LiveData<Goal> getGoal() {
        return mGoal;
    }
}
