package com.android.voyce.ui.musiciandetails;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

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
    private LiveData<Boolean> mIsSponsoring;
    private LiveData<Boolean> mIsProposalLoading;
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

    public LiveData<Boolean> getProposalLoading() {
        return mIsProposalLoading;
    }

    public LiveData<List<Proposal>> getProposals() {
        return mProposals;
    }

    public void handleFollower(String signalId, String name) {
        mRepository.handleFollower(signalId, name);
    }

    public void handleSponsor(String signalId, String name, Proposal proposal) {
        mRepository.handleSponsor(signalId, name, proposal);
    }

    public LiveData<Boolean> getIsSponsoring(String proposalId) {
        mIsProposalLoading = mRepository.getProposalLoading();
        mIsSponsoring = mRepository.getIsSponsoring(proposalId);
        return mIsSponsoring;
    }

    public LiveData<Boolean> getIsFollowing() {
        return mIsFollowing;
    }

    public LiveData<Goal> getGoal() {
        return mGoal;
    }
}
