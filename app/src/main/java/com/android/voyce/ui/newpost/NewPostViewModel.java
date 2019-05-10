package com.android.voyce.ui.newpost;

import androidx.lifecycle.ViewModel;

import com.android.voyce.data.model.Post;
import com.android.voyce.data.repository.NewPostRepository;

public class NewPostViewModel extends ViewModel {

    private NewPostRepository mRepository;

    public void init() {
        mRepository = NewPostRepository.getInstance();
    }

    public void createPost(Post post) {
        mRepository.createPost(post);
    }
}
