package com.android.voyce.ui.userprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;
import com.android.voyce.data.repository.UserProfileRepository;

import java.util.List;

public class UserProfileViewModel extends AndroidViewModel {

    private UserProfileRepository mRepository;
    private LiveData<List<UserFollowingMusician>> mUserFollowingMusicians;
    private LiveData<List<UserSponsoringProposal>> mUserSponsoringProposals;

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserProfileRepository.getInstance(application);
    }

    public void init () {
        if (mUserFollowingMusicians != null) {
            return;
        }
        mUserFollowingMusicians = mRepository.getUserFollowingMusicians();
        mUserSponsoringProposals = mRepository.getUserSponsoringProposals();
    }

    public LiveData<List<UserFollowingMusician>> getUserFollowingMusicians() {
        return mUserFollowingMusicians;
    }

    public LiveData<List<UserSponsoringProposal>> getUserSponsoringProposals() {
        return mUserSponsoringProposals;
    }
}
