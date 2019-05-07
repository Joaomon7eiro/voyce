package com.android.voyce.ui.signup;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.voyce.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpStepOneFragment extends Fragment {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private boolean mIsNameValid = false;
    private boolean mIsEmailValid = false;
    private boolean mIsPasswordValid = false;

    private ImageView mPasswordIcon;
    private boolean mPasswordIsVisible = false;

    private Button mSignUpNext;

    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_in, R.anim.fade_out);
            transaction.addToBackStack(null);
            transaction.replace(R.id.sign_up_fragments_container,
                    SignUpStepTwoFragment.newInstance(
                            mNameEditText.getText().toString(),
                            mEmailEditText.getText().toString().trim(),
                            mPasswordEditText.getText().toString()
                    ));
            transaction.commit();
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
            if (mNameEditText.getText().toString().isEmpty()) {
                mIsNameValid = false;
                mNameEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsNameValid = true;
                mNameEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
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
            String text = mEmailEditText.getText().toString();
            if (text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                mIsEmailValid = false;
                mEmailEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsEmailValid = true;
                mEmailEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            }
            checkIsValidForm();
        }
    };
    private TextWatcher mPasswordTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = mPasswordEditText.getText().toString();
            if (text.isEmpty() || text.length() < 8) {
                mIsPasswordValid = false;
                mPasswordEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            } else {
                mIsPasswordValid = true;
                mPasswordEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            }
            checkIsValidForm();
        }
    };

    private View.OnClickListener mPasswordIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mPasswordIsVisible) {
                mPasswordIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_visible));
                mPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                mPasswordIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_visible));
                mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            mPasswordEditText.setSelection(mPasswordEditText.getText().length());
            mPasswordIsVisible = !mPasswordIsVisible;
        }
    };

    public SignUpStepOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_step_one, container, false);

        mNameEditText = view.findViewById(R.id.sign_up_name_et);
        mEmailEditText = view.findViewById(R.id.sign_up_email_et);
        mPasswordEditText = view.findViewById(R.id.sign_up_password_et);

        mPasswordIcon = view.findViewById(R.id.password_visibility_icon);
        mPasswordIcon.setOnClickListener(mPasswordIconClickListener);

        mNameEditText.addTextChangedListener(mNameTextWatcher);
        mEmailEditText.addTextChangedListener(mEmailTextWatcher);
        mPasswordEditText.addTextChangedListener(mPasswordTextWatcher);

        mSignUpNext = view.findViewById(R.id.sign_up_next_button);
        mSignUpNext.setOnClickListener(mNextClickListener);
        mSignUpNext.setClickable(false);

        return view;
    }

    private void checkIsValidForm() {
        if (mIsEmailValid && mIsNameValid && mIsPasswordValid) {
            mSignUpNext.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
            mSignUpNext.setTextColor(getResources().getColor(android.R.color.white));
            mSignUpNext.setClickable(true);
        } else {
            mSignUpNext.setBackground(getResources().getDrawable(R.drawable.transparent_bordered_inactive));
            mSignUpNext.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mSignUpNext.setClickable(false);
        }
    }
}
