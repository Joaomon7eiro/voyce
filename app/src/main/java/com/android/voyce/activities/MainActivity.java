package com.android.voyce.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.android.voyce.fragments.SearchFragment;
import com.android.voyce.fragments.UserProfileFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mNoInternetConnection;
    private FrameLayout mFrameLayout;
    private int mCurrentMenuId = R.id.navigation_search;
    private boolean mIsConnected;
    private BottomNavigationView mNavigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int menuId = item.getItemId();
            if (mCurrentMenuId == menuId && mFrameLayout.getVisibility() != View.GONE) {
                return true;
            }

            checkInternetConnectivity();

            mCurrentMenuId = menuId;
            switch (menuId) {
//                case R.id.navigation_home:
//                    return true;
                case R.id.navigation_search:
                    Fragment searchFragment = new SearchFragment();
                    openFragment(searchFragment);
                    break;
                case R.id.navigation_profile:
                    Fragment profileFragment = new UserProfileFragment();
                    openFragment(profileFragment);
                    return true;
//                case R.id.navigation_musician:
//                    return true;
                default:
                    return true;
            }
            return true;
        }
    };

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

        Fragment searchFragment = new SearchFragment();
        openFragment(searchFragment);

        mNoInternetConnection = findViewById(R.id.no_connection_text);
        mFrameLayout = findViewById(R.id.fragments_container);

        checkInternetConnectivity();
    }

    public void checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            mIsConnected = true;
            mNoInternetConnection.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mIsConnected = false;
            mNoInternetConnection.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
        }
    }

    public boolean isConnected() {
        return mIsConnected;
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
