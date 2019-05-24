package com.android.voyce.ui.userprofile;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.databinding.FragmentUserSettingsBinding;
import com.android.voyce.ui.LoginActivity;
import com.android.voyce.ui.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSettingsFragment extends Fragment {


    private FragmentUserSettingsBinding mBinding;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_settings, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbarSettings);

        mBinding.toolbarSettings.setNavigationIcon(R.drawable.ic_action_back);
        mBinding.toolbarSettings.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });

        mBinding.settingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                String id = auth.getUid();

                Map<String, Object> map = new HashMap<>();
                map.put("signal_id", "");
                Task<Void> task = FirebaseFirestore.getInstance().collection("users")
                        .document(id).update(map);

                task.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null) {
                            activity.stopPlayerService();
                        }

                        OneSignal.setSubscription(false);

                        PreferenceManager.getDefaultSharedPreferences(getContext()).
                                edit().clear().apply();

                        auth.signOut();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

            }
        });

        return mBinding.getRoot();
    }

}
