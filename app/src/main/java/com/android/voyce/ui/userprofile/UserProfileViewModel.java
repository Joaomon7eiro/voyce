package com.android.voyce.ui.userprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.UserProfileRepository;

public class UserProfileViewModel extends AndroidViewModel {

    private UserProfileRepository mRepository;
    private LiveData<User> mUserLiveData;

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserProfileRepository.getInstance(application);
    }

    public void init (String id) {
        mRepository.setUserId(id);
        mUserLiveData = mRepository.getUser();
    }

    public LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }
}
