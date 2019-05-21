package com.android.voyce.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.voyce.R;
import com.android.voyce.databinding.ActivityLoginBinding;
import com.android.voyce.ui.loaduserdata.LoadUserDataActivity;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityLoginBinding mBinding;

    private boolean mPasswordIsVisible = false;

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
            mBinding.loginProgressBar.setVisibility(View.VISIBLE);
            mBinding.loginButton.setVisibility(View.INVISIBLE);
            mAuth.signInWithEmailAndPassword(
                    mBinding.loginEmailEt.getText().toString().trim(),
                    mBinding.loginPasswordEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), LoadUserDataActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro na autenticação", Toast.LENGTH_SHORT).show();
                    }
                    mBinding.loginProgressBar.setVisibility(View.GONE);
                    mBinding.loginButton.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private View.OnClickListener mPasswordIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mPasswordIsVisible) {
                mBinding.loginPasswordVisibilityIcon.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_visible));
                mBinding.loginPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                mBinding.loginPasswordVisibilityIcon.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_not_visible));
                mBinding.loginPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            mBinding.loginPasswordEt.setSelection(mBinding.loginPasswordEt.getText().length());
            mPasswordIsVisible = !mPasswordIsVisible;
        }
    };

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mBinding.loginEmailEt.isFocused()) {
                    Rect outRect = new Rect();
                    mBinding.loginEmailEt.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        mBinding.loginEmailEt.clearFocus();
                        hideKeyboard(view);
                    }
                }
                if (mBinding.loginPasswordEt.isFocused()) {
                    Rect outRect = new Rect();
                    mBinding.loginPasswordEt.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        mBinding.loginPasswordEt.clearFocus();
                        hideKeyboard(view);
                    }
                }
            }
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mBinding.loginEmailEt.addTextChangedListener(loginFormTextWatcher);

        mBinding.loginContainer.setOnTouchListener(mTouchListener);

        mBinding.loginPasswordEt.addTextChangedListener(loginFormTextWatcher);

        mBinding.loginPasswordVisibilityIcon.setOnClickListener(mPasswordIconClickListener);

        mBinding.loginButton.setOnClickListener(mLoginClickListener);
        mBinding.loginButton.setClickable(false);

        mBinding.loginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void checkValidForm() {
        if (!mBinding.loginEmailEt.getText().toString().isEmpty()
                && !mBinding.loginPasswordEt.getText().toString().isEmpty()) {
            mBinding.loginButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
            mBinding.loginButton.setTextColor(getResources().getColor(android.R.color.white));
            mBinding.loginButton.setClickable(true);
        } else {
            mBinding.loginButton.setBackground(
                    getResources().getDrawable(R.drawable.transparent_bordered_inactive));
            mBinding.loginButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mBinding.loginButton.setClickable(false);
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
