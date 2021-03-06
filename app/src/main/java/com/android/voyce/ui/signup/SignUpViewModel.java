package com.android.voyce.ui.signup;

import androidx.lifecycle.ViewModel;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SignUpRepository;

public class SignUpViewModel extends ViewModel {

    private SignUpRepository mRepository;

    public void init() {
        mRepository = SignUpRepository.getInstance();
    }

    void registerUser(User user) {
        mRepository.registerUser(user);
    }
}
