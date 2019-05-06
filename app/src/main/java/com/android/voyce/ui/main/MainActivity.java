package com.android.voyce.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.LoginTesteActivity;
import com.android.voyce.ui.search.SearchFragment;
import com.android.voyce.ui.usermusicianprofile.UserMusicianProfileFragment;
import com.android.voyce.ui.userprofile.UserProfileFragment;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity implements OSSubscriptionObserver {

    private TextView mNoInternetConnection;
    private FrameLayout mFrameLayout;
    private BottomNavigationView mNavigation;
    private FloatingActionButton mFloatingButton;
    private MainViewModel mViewModel;


    private int mCurrentMenuId = R.id.navigation_search;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int menuId = item.getItemId();

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }

            if (mCurrentMenuId == menuId && mFrameLayout.getVisibility() != View.GONE) {
                return true;
            }

            mCurrentMenuId = menuId;
            visibilityEditButton(false);
            Fragment fragment;
            switch (menuId) {
//                case R.id.navigation_home:
//                    return true;
                case R.id.navigation_search:
                    setLayoutVisibility(true);
                    fragment = SearchFragment.newInstance();
                    break;
                case R.id.navigation_profile:
                    setLayoutVisibility(true);
                    fragment = UserProfileFragment.newInstance();
                    break;
                case R.id.navigation_musician:
                    setLayoutVisibility(true);
                    fragment = UserMusicianProfileFragment.newInstance();
                    break;
                default:
                    return false;
            }
            openFragment(fragment);
            return true;
        }
    };

    public void setLayoutVisibility(boolean layoutVisibility) {
        if (layoutVisibility) {
            mNoInternetConnection.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mNoInternetConnection.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
        }
    }

    public void visibilityEditButton(boolean visible) {
        if (visible) {
            ((View) mFloatingButton).setVisibility(View.VISIBLE);
        } else {
            ((View) mFloatingButton).setVisibility(View.GONE);
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragments_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: remove this
        verifyUser();

        OneSignal.addSubscriptionObserver(this);

        setContentView(R.layout.activity_main);

        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNoInternetConnection = findViewById(R.id.no_connection_text);
        mFrameLayout = findViewById(R.id.fragments_container);
        mFloatingButton = findViewById(R.id.edit_button);

        if (savedInstanceState == null) {
            Fragment searchFragment = new SearchFragment();
            openFragment(searchFragment);
        } else {
            mCurrentMenuId = savedInstanceState.getInt(Constants.KEY_CURRENT_MENU_ID);
        }
    }

    // TODO: remove this
    private void verifyUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
        if (userId == null) {
            Intent intent = new Intent(this, LoginTesteActivity.class);
            startActivity(intent);
            finish();
        } else {
            mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            mViewModel.init(userId);
            mViewModel.getUserLiveData().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(Constants.KEY_CURRENT_USER_IMAGE, user.getImage());
                    edit.putString(Constants.KEY_CURRENT_USER_NAME, user.getName());
                    edit.apply();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.KEY_CURRENT_MENU_ID, mCurrentMenuId);
    }

    private void checkInternetConnectivity() {
        if (ConnectivityHelper.isConnected(this)) {
            setLayoutVisibility(true);
        } else {
            setLayoutVisibility(false);
        }
    }

    @Override
    public void onBackPressed() {
        visibilityEditButton(false);
        checkInternetConnectivity();
        if (mNavigation.getSelectedItemId() == R.id.navigation_search) {
            super.onBackPressed();
        } else {
            mNavigation.setSelectedItemId(R.id.navigation_search);
        }
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
            // get player ID
            String id = stateChanges.getTo().getUserId();
            mViewModel.setSignalId(id);
        }

        Log.d("Debug", "onOSPermissionChanged: " + stateChanges);
    }
}
