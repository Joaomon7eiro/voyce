package com.android.voyce.ui.newpost;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.voyce.data.model.Post;
import com.android.voyce.data.repository.NewPostRepository;

public class NewPostViewModel extends ViewModel {

    private MutableLiveData<Uri> mSelectedImageUriLiveData = new MutableLiveData<>();

    LiveData<Uri> getSelectedImageUri() {
        return mSelectedImageUriLiveData;
    }

    void setSelectedImageUri(Uri selectedImageUri) {
        mSelectedImageUriLiveData.setValue(selectedImageUri);
    }

    private NewPostRepository mRepository;

    public void init() {
        mRepository = NewPostRepository.getInstance();
    }

    void createPost(Post post) {
        mRepository.createPost(post);
    }
}
