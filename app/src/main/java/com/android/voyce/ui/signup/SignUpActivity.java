package com.android.voyce.ui.signup;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.android.voyce.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.sign_up_fragments_container, new SignUpStepOneFragment());
            transaction.commit();
        }
    }
}
