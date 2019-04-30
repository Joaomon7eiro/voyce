package com.android.voyce.ui.usermusicianprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.UserMusicianRepository;

import java.util.List;

public class UserMusicianProfileViewModel extends AndroidViewModel {

    private LiveData<User> mUserLiveData;
    private LiveData<Goal> mGoalLiveData;
    private LiveData<List<Proposal>> mProposals;
    private UserMusicianRepository mRepository;

    public UserMusicianProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserMusicianRepository.getInstance(application);
    }

    public void init(String userId) {
        mRepository.setUserId(userId);
        mUserLiveData = mRepository.getUser();
        mGoalLiveData = mRepository.getGoalValue();
        mProposals = mRepository.getProposals();
    }

    @NonNull
    public LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }

    public LiveData<Goal> getGoalLiveData() {
        return mGoalLiveData;
    }

    public LiveData<List<Proposal>> getProposals() {
        return mProposals;
    }
}
