package com.android.voyce.ui.newpost;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.voyce.databinding.ActivityNewPostBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;
import com.android.voyce.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class NewPostActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    private NewPostViewModel mViewModel;
    private boolean mPublishEnabled = false;
    private String mUserId;
    private String mUserName;
    private String mUserImage;
    private StorageReference mPostImagesStorage;
    private ActivityNewPostBinding mBinding;
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_post);


        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mBinding.newPostContent.cancelImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setSelectedImageUri(null);
                mBinding.newPostContent.postImageIv.setVisibility(View.GONE);
                mBinding.newPostContent.cancelImageIcon.setVisibility(View.GONE);
            }
        });

        mBinding.newPostContent.postTextEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPublishEnabled = mBinding.newPostContent.postTextEt.getText().toString().length() > 0;
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPostImagesStorage = FirebaseStorage.getInstance().getReference().child("posts_images");

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                mBinding.newPostContent.postProgressBar.setVisibility(View.VISIBLE);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(NewPostViewModel.class);
        mViewModel.init();
        mViewModel.getSelectedImageUri().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                if (uri != null) {
                    mSelectedImageUri = uri;
                    Picasso.get().load(mSelectedImageUri).into(mBinding.newPostContent.postImageIv);
                    mBinding.newPostContent.postImageIv.setVisibility(View.VISIBLE);
                    mBinding.newPostContent.cancelImageIcon.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (data != null) {
                mViewModel.setSelectedImageUri(data.getData());
            }
            mBinding.newPostContent.postProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPublishEnabled) {
            menu.findItem(R.id.publish).setEnabled(true);
        } else {
            menu.findItem(R.id.publish).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.publish) {
            if (mSelectedImageUri != null) {
                final StorageReference imageRef = mPostImagesStorage.child(mSelectedImageUri.getLastPathSegment());
                mBinding.newPostContent.postProgressBar.setVisibility(View.VISIBLE);
                mBinding.newPostContainer.setAlpha(0.9f);
                imageRef.putFile(mSelectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mBinding.newPostContent.postProgressBar.setVisibility(View.GONE);
                                        mBinding.newPostContainer.setAlpha(1);
                                        if (uri != null) {
                                            publishPost(uri.toString());
                                        }
                                    }
                                });
                            }
                        });
            } else {
                publishPost(null);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void publishPost(String imageUrl) {
        Post post = new Post();
        post.setText(mBinding.newPostContent.postTextEt.getText().toString());
        post.setUser_id(mUserId);
        post.setUser_image(mUserImage);
        post.setImage(imageUrl);
        post.setUser_name(mUserName);
        mViewModel.createPost(post);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mBinding.newPostContent.postProgressBar.getVisibility() == View.VISIBLE) return false;
        return super.dispatchTouchEvent(ev);
    }
}
