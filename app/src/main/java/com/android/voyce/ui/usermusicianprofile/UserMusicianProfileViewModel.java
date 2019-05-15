package com.android.voyce.ui.usermusicianprofile;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.repository.UserMusicianRepository;

import java.util.List;

public class UserMusicianProfileViewModel extends AndroidViewModel {

    private LiveData<User> mUserLiveData;
    private LiveData<Goal> mGoalLiveData;
    private LiveData<List<Proposal>> mProposals;
    private LiveData<List<UserFollowingMusician>> mFollowersLiveData;
    private UserMusicianRepository mRepository;

    public UserMusicianProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserMusicianRepository.getInstance(application);
    }

    public void init(String userId) {
        if (mUserLiveData != null) {
            return;
        }
        mRepository.setUserId(userId);
        mUserLiveData = mRepository.getUser();
        mGoalLiveData = mRepository.getGoalValue();
        mProposals = mRepository.getProposals();
        mFollowersLiveData = mRepository.getFollowers();
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

    public LiveData<List<UserFollowingMusician>> getFollowers() {
        return mFollowersLiveData;
    }
}
