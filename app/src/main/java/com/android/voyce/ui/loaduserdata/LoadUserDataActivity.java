package com.android.voyce.ui.loaduserdata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.LoginTesteActivity;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadUserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_load_user_data_activity);
        verifyUser();
    }

    private void verifyUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            LoadUserDataViewModel viewModel = ViewModelProviders.of(this).get(LoadUserDataViewModel.class);
            viewModel.init(currentUser.getUid());
            viewModel.getUserLiveData().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (!user.getId().equals(currentUser.getUid())) return;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(Constants.KEY_CURRENT_USER_ID, currentUser.getUid());
                    edit.putString(Constants.KEY_CURRENT_USER_IMAGE, user.getImage());
                    edit.putString(Constants.KEY_CURRENT_USER_NAME, user.getName());
                    edit.putString(Constants.KEY_CURRENT_USER_CITY, user.getCity());
                    edit.putString(Constants.KEY_CURRENT_USER_STATE, user.getState());
                    edit.putInt(Constants.KEY_CURRENT_USER_TYPE, user.getType());
                    edit.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(this, LoginTesteActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
