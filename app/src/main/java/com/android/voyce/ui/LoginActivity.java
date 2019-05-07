package com.android.voyce.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.voyce.R;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mEmail;
    private EditText mPassword;
    private ImageView mPasswordIcon;
    private ProgressBar mProgressBar;
    private ConstraintLayout mContainer;
    private boolean mPasswordIsVisible = false;
    private Button mLogin;

    private TextWatcher loginFormTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkValidForm();
        }
    };

    private View.OnClickListener mLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(
                    mEmail.getText().toString().trim(),
                    mPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email ou senha estão incorretos", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private View.OnClickListener mPasswordIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mPasswordIsVisible) {
                mPasswordIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_visible));
                mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                mPasswordIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_visible));
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            mPassword.setSelection(mPassword.getText().length());
            mPasswordIsVisible = !mPasswordIsVisible;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.login_email_et);
        mEmail.addTextChangedListener(loginFormTextWatcher);
        mPassword = findViewById(R.id.login_password_et);
        mPassword.addTextChangedListener(loginFormTextWatcher);

        mPasswordIcon = findViewById(R.id.login_password_visibility_icon);
        mPasswordIcon.setOnClickListener(mPasswordIconClickListener);

        mContainer = findViewById(R.id.login_container);
        mProgressBar = findViewById(R.id.login_progress_bar);

        mLogin = findViewById(R.id.login_button);
        mLogin.setOnClickListener(mLoginClickListener);
        mLogin.setClickable(false);

        TextView signUp = findViewById(R.id.login_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkValidForm() {
        if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
            mLogin.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
            mLogin.setTextColor(getResources().getColor(android.R.color.white));
            mLogin.setClickable(true);
        } else {
            mLogin.setBackground(getResources().getDrawable(R.drawable.transparent_bordered_inactive));
            mLogin.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mLogin.setClickable(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
