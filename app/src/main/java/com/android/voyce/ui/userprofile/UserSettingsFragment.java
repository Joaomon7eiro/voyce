package com.android.voyce.ui.userprofile;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSettingsFragment extends Fragment {


    public UserSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        TextView logout = view.findViewById(R.id.settings_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth =  FirebaseAuth.getInstance();
                String id = auth.getUid();
                auth.signOut();

                OneSignal.setSubscription(false);

                Map<String, Object> map = new HashMap<>();
                map.put("signal_id", "");
                FirebaseFirestore.getInstance().collection("users")
                        .document(id).update(map);

                PreferenceManager.getDefaultSharedPreferences(getContext()).
                        edit().clear().apply();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

}