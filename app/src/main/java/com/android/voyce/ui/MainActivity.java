package com.android.voyce.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.ui.search.SearchFragment;
import com.android.voyce.ui.userprofile.UserProfileFragment;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private TextView mNoInternetConnection;
    private FrameLayout mFrameLayout;
    private BottomNavigationView mNavigation;

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
            switch (menuId) {
//                case R.id.navigation_home:
//                    return true;
                case R.id.navigation_search:
                    checkInternetConnectivity();
                    Fragment searchFragment = new SearchFragment();
                    openFragment(searchFragment);
                    return true;
                case R.id.navigation_profile:
                    setLayoutVisibility(true);
                    Fragment profileFragment = new UserProfileFragment();
                    openFragment(profileFragment);
                    return true;
//                case R.id.navigation_musician:
//                    return true;
                default:
                    return false;
            }
        }
    };

    private void setLayoutVisibility(boolean layoutVisibility) {
        if (layoutVisibility) {
            mNoInternetConnection.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mNoInternetConnection.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_main);

        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNoInternetConnection = findViewById(R.id.no_connection_text);
        mFrameLayout = findViewById(R.id.fragments_container);

        if (savedInstanceState == null) {
            Fragment searchFragment = new SearchFragment();
            openFragment(searchFragment);
            checkInternetConnectivity();
        } else {
            mCurrentMenuId = savedInstanceState.getInt(Constants.KEY_CURRENT_MENU_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.KEY_CURRENT_MENU_ID, mCurrentMenuId);
    }

    public void checkInternetConnectivity() {
        if (ConnectivityHelper.isConnected(this)) {
            setLayoutVisibility(true);
        } else {
            setLayoutVisibility(false);
        }
    }

    @Override
    public void onBackPressed() {
        checkInternetConnectivity();
        if (mNavigation.getSelectedItemId() == R.id.navigation_search) {
            super.onBackPressed();
        } else {
            mNavigation.setSelectedItemId(R.id.navigation_search);
        }
    }
}