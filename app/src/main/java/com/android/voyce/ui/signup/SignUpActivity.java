package com.android.voyce.ui.signup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.android.voyce.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_sign_up);
    }
}
