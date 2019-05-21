package com.android.voyce.ui.loaduserdata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.LoadUserDataRepository;

public class LoadUserDataViewModel extends ViewModel {
    private LiveData<User> mUserLiveData;

    public void init(String id) {
        if (mUserLiveData != null) {
            return;
        }
        LoadUserDataRepository repository = LoadUserDataRepository.getInstance();
        mUserLiveData = repository.getUser(id);
    }

    LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }
}
