package com.android.voyce.ui.userprofile;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

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

    LiveData<List<UserFollowingMusician>> getUserFollowingMusicians() {
        return mUserFollowingMusicians;
    }

    LiveData<List<UserSponsoringProposal>> getUserSponsoringProposals() {
        return mUserSponsoringProposals;
    }
}
