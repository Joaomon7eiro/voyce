package com.android.voyce.ui.usermusicianprofile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.UserRepository;

import java.util.List;

public class UserMusicianProfileViewModel extends ViewModel {

    private LiveData<User> mUserLiveData;
    private LiveData<Goal> mGoalLiveData;
    private LiveData<Boolean> mIsLoading;
    private LiveData<List<Proposal>> mProposals;

    public void init(String userId) {
        UserRepository repository = UserRepository.getInstance(userId);
        mUserLiveData = repository.getUser();
        mGoalLiveData = repository.getGoalValue();
        mIsLoading = repository.getLoadingState();
        mProposals = repository.getProposals();
    }

    @NonNull
    public LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }

    public LiveData<Goal> getGoalLiveData() {
        return mGoalLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<List<Proposal>> getProposals() {
        return mProposals;
    }
}
