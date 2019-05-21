package com.android.voyce.ui.signup;



import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.voyce.R;
import com.android.voyce.data.model.City;
import com.android.voyce.data.model.State;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.loaduserdata.LoadUserDataActivity;
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
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpStepTwoFragment extends Fragment {

    private FirebaseAuth mAuth;

    private ConstraintLayout mContainer;
    private ProgressBar mProgressBar;

    private String mName;
    private String mEmail;
    private String mPassword;

    private List<City> mCitiesList = new ArrayList<>();
    private List<State> mStatesList = new ArrayList<>();

    private boolean mIsCitySelected = false;
    private boolean mIsValidDate = false;

    private AutoCompleteTextView mCityAutoComplete;
    private EditText mState;
    private EditText mDateOfBirth;

    private RadioGroup mRadioGroup;
    private SignUpViewModel mViewModel;

    private Button mSignUp;

    private View.OnClickListener mSignUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        int radioId = mRadioGroup.getCheckedRadioButtonId();
                        RadioButton genderRadio = mRadioGroup.findViewById(radioId);
                        int genderInt = genderInt(genderRadio.getText().toString().toLowerCase());

                        User user = new User();
                        user.setId(mAuth.getCurrentUser().getUid());
                        user.setName(mName.toLowerCase());
                        user.setEmail(mEmail);
                        user.setGender(genderInt);
                        user.setCity(mCityAutoComplete.getText().toString().toLowerCase());
                        user.setState(mState.getText().toString().toLowerCase());

                        mViewModel.registerUser(user);

                        Intent intent = new Intent(getContext(), LoadUserDataActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Cadastro falhou", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                }
            });

        }
    };

    private TextWatcher mCityTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mIsCitySelected = false;
            mCityAutoComplete.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            mState.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            mState.setText("");
            checkIsValidForm();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private TextWatcher mDateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mDateOfBirth.getText().toString().length() == 10) {
                mIsValidDate = validateDate(mDateOfBirth.getText().toString());
                if (mIsValidDate) {
                    mDateOfBirth.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                } else {
                    mDateOfBirth.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                }
            } else {
                mDateOfBirth.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                mIsValidDate = false;
            }
            checkIsValidForm();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private Boolean validateDate(String dateText) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            format.setLenient(false);
            Date date = format.parse(dateText);

            Calendar calendarDate = new GregorianCalendar();
            calendarDate.setTime(date);
            int year = calendarDate.get(Calendar.YEAR);
            int month = calendarDate.get(Calendar.MONTH);
            int day = calendarDate.get(Calendar.DAY_OF_MONTH);

            Calendar calendarToday = new GregorianCalendar();
            calendarToday.setTime(new Timestamp(System.currentTimeMillis()));
            int yearToday = calendarToday.get(Calendar.YEAR);
            int monthToday = calendarToday.get(Calendar.MONTH);
            int dayToday = calendarToday.get(Calendar.DAY_OF_MONTH);

            if (yearToday - year > 14) {
                return true;
            } else if (yearToday - year == 14) {
                return monthToday - month > 0 || (monthToday - month == 0 && dayToday - day >= 0);
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    private AdapterView.OnItemClickListener mCityClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            City city = (City) parent.getAdapter().getItem(position);

            for (State state : mStatesList) {
                if (state.getId().equals(city.getStateId())) {
                    mState.setText(state.getInitials());
                    mIsCitySelected = true;
                    mCityAutoComplete.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                    mState.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                    checkIsValidForm();
                    break;
                }
            }
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mDateOfBirth.isFocused()) {
                    Rect outRect = new Rect();
                    mDateOfBirth.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        mDateOfBirth.clearFocus();
                        hideKeyboard(view);
                    }
                }
                if (mCityAutoComplete.isFocused()) {
                    Rect outRect = new Rect();
                    mCityAutoComplete.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        mCityAutoComplete.clearFocus();
                        hideKeyboard(view);
                    }
                }
            }
            return false;
        }
    };

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_step_two, container, false);

        view.setOnTouchListener(mTouchListener);

        mContainer = view.findViewById(R.id.sign_up_container);
        mProgressBar = view.findViewById(R.id.sign_up_progress_bar);

        mRadioGroup = view.findViewById(R.id.gender_container);

        mCityAutoComplete = view.findViewById(R.id.sign_up_city);
        mCityAutoComplete.setOnItemClickListener(mCityClickListener);
        mCityAutoComplete.addTextChangedListener(mCityTextWatcher);
        setupAutoComplete();

        mState = view.findViewById(R.id.sign_up_state);

        mDateOfBirth = view.findViewById(R.id.sign_up_date_of_birth);
        mDateOfBirth.addTextChangedListener(mDateTextWatcher);

        mSignUp = view.findViewById(R.id.sign_up_login_button);
        mSignUp.setOnClickListener(mSignUpClickListener);
        mSignUp.setClickable(false);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void setupAutoComplete() {

        JSONArray jsonCities = null;
        JSONArray jsonStates = null;
        try {
            jsonCities = new JSONArray(getJson("cidades.json"));
            jsonStates = new JSONArray(getJson("estados.json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonCities.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonCities.get(i);
                City city = new City();
                city.setId(jsonObject.getString("ID"));
                city.setName(jsonObject.getString("Nome"));
                city.setStateId(jsonObject.getString("Estado"));
                mCitiesList.add(city);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < jsonStates.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonStates.get(i);
                State state = new State();
                state.setId(jsonObject.getString("ID"));
                state.setInitials(jsonObject.getString("Sigla"));
                state.setName(jsonObject.getString("Nome"));
                mStatesList.add(state);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, mCitiesList);
        mCityAutoComplete.setAdapter(cityAdapter);
    }

    private String getJson(String filename) {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private int genderInt(String genderText) {
        switch (genderText) {
            case Constants.GENDER_MALE:
                return Constants.GENDER_MALE_INT;
            case Constants.GENDER_FEMALE:
                return Constants.GENDER_FEMALE_INT;
            case Constants.GENDER_OTHERS:
                return Constants.GENDER_OTHERS_INT;
            default:
                return Constants.GENDER_MALE_INT;
        }
    }

    private void checkIsValidForm() {
        if (mIsCitySelected && mIsValidDate) {
            mSignUp.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
            mSignUp.setTextColor(getResources().getColor(android.R.color.white));
            mSignUp.setClickable(true);
        } else {
            mSignUp.setBackground(getResources().getDrawable(R.drawable.transparent_bordered_inactive));
            mSignUp.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mSignUp.setClickable(false);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
