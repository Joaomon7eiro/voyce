package com.android.voyce.ui.signup;

import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SignUpRepository;

public class SignUpViewModel extends ViewModel {

    private SignUpRepository mRepository;

    public void init() {
        mRepository = SignUpRepository.getInstance();
    }

    public void registerUser(User user) {
        mRepository.registerUser(user);
    }
}
