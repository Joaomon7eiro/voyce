package com.android.voyce.ui.signup;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.android.voyce.R;
import com.android.voyce.databinding.FragmentSignUpStepOneBinding;

import static com.android.voyce.ui.signup.SignUpStepOneFragmentDirections.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpStepOneFragment extends Fragment {

    private boolean mIsNameValid = false;
    private boolean mIsEmailValid = false;
    private boolean mIsPasswordValid = false;
    private boolean mPasswordIsVisible = false;

    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSignUpStepOneFragmentToSignUpStepTwoFragment action =
                    actionSignUpStepOneFragmentToSignUpStepTwoFragment(
                    mBinding.signUpNameEt.getText().toString(),
                    mBinding.signUpEmailEt.getText().toString(),
                    mBinding.signUpPasswordEt.getText().toString()
            );
            Navigation.findNavController(mBinding.getRoot())
                    .navigate(action);
        }
    };

    private TextWatcher mNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mBinding.signUpNameEt.getText().toString().isEmpty()) {
                mIsNameValid = false;
                mBinding.signUpNameEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsNameValid = true;
                mBinding.signUpNameEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            }
            checkIsValidForm();
        }
    };
    private TextWatcher mEmailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = mBinding.signUpEmailEt.getText().toString();
            if (text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                mIsEmailValid = false;
                mBinding.signUpEmailEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsEmailValid = true;
                mBinding.signUpEmailEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            }
            checkIsValidForm();
        }
    };
    private TextWatcher mPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = mBinding.signUpPasswordEt.getText().toString();
            if (text.isEmpty() || text.length() < 8) {
                mIsPasswordValid = false;
                mBinding.signUpPasswordEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsPasswordValid = true;
                mBinding.signUpPasswordEt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            }
            checkIsValidForm();
        }
    };

    private View.OnClickListener mPasswordIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mPasswordIsVisible) {
                mBinding.passwordVisibilityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_visible));
                mBinding.signUpPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                mBinding.passwordVisibilityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_visible));
                mBinding.signUpPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            mBinding.signUpPasswordEt.setSelection(mBinding.signUpPasswordEt.getText().length());
            mPasswordIsVisible = !mPasswordIsVisible;
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mBinding.signUpNameEt.isFocused()) {
                    Rect outRect = new Rect();
                    mBinding.signUpNameEt.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        mBinding.signUpNameEt.clearFocus();
                        hideKeyboard(view);
                    }
                }
                if (mBinding.signUpEmailEt.isFocused()) {
                    Rect outRect = new Rect();
                    mBinding.signUpEmailEt.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        mBinding.signUpEmailEt.clearFocus();
                        hideKeyboard(view);
                    }
                }
                if (mBinding.signUpPasswordEt.isFocused()) {
                    Rect outRect = new Rect();
                    mBinding.signUpPasswordEt.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        mBinding.signUpPasswordEt.clearFocus();
                        hideKeyboard(view);
                    }
                }
            }
            return false;
        }
    };
    private FragmentSignUpStepOneBinding mBinding;

    public SignUpStepOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_step_one, container, false);

        mBinding.getRoot().setOnTouchListener(mTouchListener);

        mBinding.passwordVisibilityIcon.setOnClickListener(mPasswordIconClickListener);

        mBinding.signUpNameEt.addTextChangedListener(mNameTextWatcher);
        mBinding.signUpEmailEt.addTextChangedListener(mEmailTextWatcher);
        mBinding.signUpPasswordEt.addTextChangedListener(mPasswordTextWatcher);

        mBinding.signUpNextButton.setOnClickListener(mNextClickListener);
        mBinding.signUpNextButton.setClickable(false);

        return mBinding.getRoot();
    }

    private void checkIsValidForm() {
        if (mIsEmailValid && mIsNameValid && mIsPasswordValid) {
            mBinding.signUpNextButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
            mBinding.signUpNextButton.setTextColor(getResources().getColor(android.R.color.white));
            mBinding.signUpNextButton.setClickable(true);
        } else {
            mBinding.signUpNextButton.setBackground(getResources().getDrawable(R.drawable.transparent_bordered_inactive));
            mBinding.signUpNextButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mBinding.signUpNextButton.setClickable(false);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
