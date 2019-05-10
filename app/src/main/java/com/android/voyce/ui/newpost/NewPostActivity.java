package com.android.voyce.ui.newpost;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
    private EditText mPostText;
    private boolean mPublishEnabled = false;
    private String mUserId;
    private String mUserName;
    private String mUserImage;
    private ImageView mPostImage;
    private ImageView mCancelImageIcon;
    private ProgressBar mProgressBar;
    private StorageReference mPostImagesStorage;
    private CoordinatorLayout mContainer;
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContainer = findViewById(R.id.new_post_container);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mProgressBar = findViewById(R.id.post_progress_bar);
        mCancelImageIcon = findViewById(R.id.cancel_image_icon);
        mCancelImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedImageUri = null;
                mPostImage.setVisibility(View.GONE);
                mCancelImageIcon.setVisibility(View.GONE);
            }
        });

        mPostImage = findViewById(R.id.post_image_iv);
        mPostText = findViewById(R.id.post_text_et);
        mPostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPublishEnabled = mPostText.getText().toString().length() > 0;
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPostImagesStorage = FirebaseStorage.getInstance().getReference().child("posts_images");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(NewPostViewModel.class);
        mViewModel.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (data != null) {
                mSelectedImageUri = data.getData();
                Picasso.get().load(mSelectedImageUri).into(mPostImage);
                mPostImage.setVisibility(View.VISIBLE);
                mCancelImageIcon.setVisibility(View.VISIBLE);
            }
            mProgressBar.setVisibility(View.GONE);
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
                mProgressBar.setVisibility(View.VISIBLE);
                mContainer.setAlpha(0.9f);
                imageRef.putFile(mSelectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mProgressBar.setVisibility(View.GONE);
                                        mContainer.setAlpha(1);
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
        post.setText(mPostText.getText().toString());
        post.setUser_id(mUserId);
        post.setUser_image(mUserImage);
        post.setImage(imageUrl);
        post.setUser_name(mUserName);
        mViewModel.createPost(post);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mProgressBar.getVisibility() == View.VISIBLE) return false;
        return super.dispatchTouchEvent(ev);
    }
}
