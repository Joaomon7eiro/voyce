package com.android.voyce.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.ui.userprofile.UserEditFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;
import com.android.voyce.ui.feed.FeedFragment;
import com.android.voyce.ui.LoginTesteActivity;
import com.android.voyce.ui.search.SearchFragment;
import com.android.voyce.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity implements OSSubscriptionObserver {

    private TextView mNoInternetConnection;
    private FrameLayout mFrameLayout;
    private BottomNavigationView mNavigation;
    private MainViewModel mViewModel;
    private boolean mHasFragment;
    private NavController mNavController;
    private static final int RC_PHOTO_PICKER = 2;

    private static final String KEY_HAS_FRAGMENT = "key_has_fragment";

    private BottomNavigationView.OnNavigationItemReselectedListener mBottomNavItemReselectListener =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) {
                    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.main_content);
                    if (navHostFragment != null) {
                        Fragment fragment = navHostFragment.getChildFragmentManager()
                                .getFragments()
                                .get(0);
                        int backStackCount = navHostFragment
                                .getChildFragmentManager()
                                .getBackStackEntryCount();

                        if (item.getItemId() == R.id.navigation_feed) {
                            if (backStackCount > 0) {
                                mNavController.popBackStack();
                                return;
                            }
                        } else {
                            if (backStackCount > 1) {
                                mNavController.popBackStack();
                                return;
                            }
                        }

                        switch (item.getItemId()) {
                            case R.id.navigation_feed:
                                if (fragment instanceof FeedFragment) {
                                    ((FeedFragment) fragment).scrollToStart();
                                }
                                break;
                            case R.id.navigation_search:
                                if (fragment instanceof SearchFragment) {
                                    ((SearchFragment) fragment).openSearchResults();
                                }
                                break;
                            default:
                        }
                    }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);

        verifyUser();

        OneSignal.addSubscriptionObserver(this);

        mNavigation = findViewById(R.id.navigation);
        mNavController = Navigation.findNavController(this, R.id.main_content);

        mNavigation.setOnNavigationItemReselectedListener(mBottomNavItemReselectListener);

        mNoInternetConnection = findViewById(R.id.no_connection_text);
        mFrameLayout = findViewById(R.id.main_content);

        if (savedInstanceState != null) {
            mHasFragment = savedInstanceState.getBoolean(KEY_HAS_FRAGMENT);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("key_has_fragment", mHasFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER) {
            if (data != null) {
                Fragment fragment = getSupportFragmentManager()
                        .findFragmentById(R.id.main_content)
                        .getChildFragmentManager()
                        .getFragments()
                        .get(0);

                if (fragment instanceof UserEditFragment) {
                    ((UserEditFragment) fragment).setImageUri(data.getData());
                }
            }
        }
    }

    private void verifyUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            mViewModel.init(currentUser.getUid());
            mViewModel.getUserLiveData().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (!user.getId().equals(currentUser.getUid())) return;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(Constants.KEY_CURRENT_USER_ID, currentUser.getUid());
                    edit.putString(Constants.KEY_CURRENT_USER_IMAGE, user.getImage());
                    edit.putString(Constants.KEY_CURRENT_USER_NAME, user.getName());
                    edit.putString(Constants.KEY_CURRENT_USER_CITY, user.getCity());
                    edit.putString(Constants.KEY_CURRENT_USER_STATE, user.getState());
                    edit.putInt(Constants.KEY_CURRENT_USER_TYPE, user.getType());
                    edit.apply();

                    NavigationUI.setupWithNavController(mNavigation, mNavController);
                }
            });
            OneSignal.setSubscription(true);
        } else {
            Intent intent = new Intent(this, LoginTesteActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
            // get user id
            String id = stateChanges.getTo().getUserId();
            mViewModel.setSignalId(id);
        }

        Log.d("Debug", "onOSPermissionChanged: " + stateChanges);
    }
}
