package com.android.voyce.ui.signup;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpStepTwoFragment extends Fragment {

    private FirebaseAuth mAuth;

    private String mName;
    private String mEmail;
    private String mPassword;

    private Spinner mCitySpinner;
    private EditText mStateEditText;

    private RadioGroup mRadioGroup;
    private SignUpViewModel mViewModel;
    private String mCity;

    public SignUpStepTwoFragment() {
        // Required empty public constructor
    }

    public static SignUpStepTwoFragment newInstance(String name, String email, String password) {
        SignUpStepTwoFragment fragment = new SignUpStepTwoFragment();

        Bundle args = new Bundle();

        args.putString("sign_up_name", name);
        args.putString("sign_up_email", email);
        args.putString("sign_up_password", password);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mName = args.getString("sign_up_name", null);
            mEmail = args.getString("sign_up_email", null);
            mPassword = args.getString("sign_up_password", null);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        mViewModel.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_step_two, container, false);

        mCitySpinner = view.findViewById(R.id.sign_up_city_et);
        setupSpinner();

        mStateEditText = view.findViewById(R.id.sign_up_state_et);

        Button signUp = view.findViewById(R.id.sign_up_login_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            int radioId = mRadioGroup.getCheckedRadioButtonId();
                            RadioButton genreRadio = mRadioGroup.findViewById(radioId);

                            int genreInt = genreInt(genreRadio.getText().toString());

                            User user = new User();
                            user.setId(mAuth.getCurrentUser().getUid());
                            user.setName(mName);
                            user.setEmail(mEmail);
                            user.setGenre(genreInt);
                            user.setCity(mCity);
                            user.setState(mStateEditText.getText().toString().trim());

                            mViewModel.registerUser(user);

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString(Constants.KEY_CURRENT_USER_ID, mAuth.getCurrentUser().getUid());
                            edit.apply();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Cadastro falhou", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mRadioGroup = view.findViewById(R.id.genre_container);


        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void setupSpinner() {

        mCitySpinner = new Spinner(getContext());
        List<String> cities = new ArrayList<>();

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                cities.add(jsonObject.getString("Nome"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCitySpinner.setAdapter(adapter);
    }

    private String getJson() {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open("cidades.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private int genreInt(String genreText) {
        switch (genreText) {
            case "masculino":
                return 0;
            case "feminino":
                return 1;
            case "outros":
                return 2;
            default:
                return 0;
        }
    }

}
