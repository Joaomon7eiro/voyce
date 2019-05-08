package com.android.voyce.ui.feed;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;
import com.android.voyce.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class NewPostActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    private NewPostViewModel mViewModel;
    private EditText mPostText;
    private boolean mPublishEnabled = false;
    private String mUserId;
    private String mUserName;
    private String mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mPostText = findViewById(R.id.post_text_et);
        mPostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPostText.getText().toString().length() > 0) {
                    mPublishEnabled = true;
                } else {
                    mPublishEnabled = false;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(NewPostViewModel.class);
        mViewModel.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            Post post = new Post();
            post.setText(mPostText.getText().toString());
            post.setUser_id(mUserId);
            post.setUser_image(mUserImage);
            post.setUser_name(mUserName);
            mViewModel.createPost(post);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
