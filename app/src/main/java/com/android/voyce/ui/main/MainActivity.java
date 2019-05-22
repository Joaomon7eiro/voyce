package com.android.voyce.ui.main;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.AudioPlayerService;
import com.android.voyce.databinding.ActivityMainBinding;
import com.android.voyce.ui.userprofile.UserEditFragment;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.MenuItem;

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

    private MainViewModel mViewModel;
    private NavController mNavController;
    private static final int RC_PHOTO_PICKER = 2;
    private AudioPlayerService.PlayerBinder mPlayerBinder;
    private ActivityMainBinding mBinding;
    private Intent mPlayerServiceIntent;
    private boolean mPlayerHasStarted;

    private ServiceConnection mPlayerServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayerBinder = (AudioPlayerService.PlayerBinder) service;
            mBinding.playerView.setPlayer(mPlayerBinder.getPlayer());
            mBinding.playerView.show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayerBinder = null;
        }
    };

    public void playSingle(String userId, String songId) {
        if (mPlayerBinder != null) {
            mPlayerBinder.playSingles(userId, songId);
        }
    }

    public void startPlayerService() {
        if (!mPlayerHasStarted) {
            Util.startForegroundService(this, mPlayerServiceIntent);
            mPlayerHasStarted = true;
        }
    }

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

                        if (item.getItemId() == R.id.navigation_feed && backStackCount > 0) {
                            mNavController.popBackStack();
                            return;
                        } else if (backStackCount > 1) {
                            mNavController.popBackStack();
                            return;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUser();

        mNavController = Navigation.findNavController(this, R.id.main_content);
        mBinding.bottomNavigation.setOnNavigationItemReselectedListener(mBottomNavItemReselectListener);
        NavigationUI.setupWithNavController(mBinding.bottomNavigation, mNavController);

        mPlayerServiceIntent = new Intent(this, AudioPlayerService.class);
        bindService(mPlayerServiceIntent, mPlayerServiceConnection, BIND_AUTO_CREATE);
        mBinding.playerView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mPlayerServiceConnection);
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

    private void setUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            mViewModel.init(currentUser.getUid());

            OneSignal.setSubscription(true);
            OneSignal.addSubscriptionObserver(this);

            mViewModel.getUserLiveData().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
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
                    }
                }
            });
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
