package com.android.voyce.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.android.voyce.data.repository.MainRepository;

public class MainViewModel extends AndroidViewModel {
    private MainRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = MainRepository.getInstance(application);
    }

    public void init(String id) {
        mRepository.setUserId(id);
        mRepository.startUser();
    }
}
