package com.android.voyce.ui.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.voyce.data.model.User;
import com.android.voyce.data.repository.SearchResultsRepository;

import java.util.List;

public class SearchResultsViewModel extends ViewModel {

    private LiveData<List<User>> mUserList;
    private SearchResultsRepository mRepository;
    private LiveData<Boolean> mLoading;

    public void init() {
        mRepository = SearchResultsRepository.getInstance();
        mLoading = mRepository.getIsLoading();
        mUserList = mRepository.getUsers(null);
    }

    public void queryUsers(String query) {
        mUserList = mRepository.getUsers(query);
    }
    public LiveData<List<User>> getUserList() {
        return mUserList;
    }

    public LiveData<Boolean> getLoading() {
        return mLoading;
    }
}
