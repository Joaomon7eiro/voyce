package com.android.voyce.ui.userprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.repository.UserProfileRepository;

import java.util.List;

public class UserProfileViewModel extends AndroidViewModel {

    private UserProfileRepository mRepository;
    private LiveData<User> mUserLiveData;
    private LiveData<List<UserFollowingMusician>> mUserFollowingMusicians;

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserProfileRepository.getInstance(application);
    }

    public void init (String id) {
        mRepository.setUserId(id);
        mUserLiveData = mRepository.getUser();
        mUserFollowingMusicians = mRepository.getUserFollowingMusicians();
    }

    public LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }

    public LiveData<List<UserFollowingMusician>> getUserFollowingMusicians() {
        return mUserFollowingMusicians;
    }
}
