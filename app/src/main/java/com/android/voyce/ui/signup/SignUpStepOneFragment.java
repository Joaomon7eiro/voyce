package com.android.voyce.ui.signup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.voyce.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpStepOneFragment extends Fragment {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    public SignUpStepOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_step_one, container, false);

        mNameEditText = view.findViewById(R.id.sign_up_name_et);
        mEmailEditText = view.findViewById(R.id.sign_up_email_et);
        mPasswordEditText = view.findViewById(R.id.sign_up_password_et);

        Button signUpNext = view.findViewById(R.id.sign_up_next_button);

        signUpNext.setOnClickListener(new View.OnClickListener() {
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
        });

        return view;
    }

}
