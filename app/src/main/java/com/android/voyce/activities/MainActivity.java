package com.android.voyce.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.voyce.R;
import com.android.voyce.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private int mCurrentMenuId = R.id.navigation_home;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int menuId = item.getItemId();
            if (mCurrentMenuId == menuId) {
                return true;
            }
            mCurrentMenuId = menuId;
            switch (menuId) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_search:
                    Fragment searchFragment = new SearchFragment();
                    openFragment(searchFragment);
                    break;
                case R.id.navigation_profile:
                    return true;
                case R.id.navigation_musician:
                    return true;
            }
            return true;
        }
    };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragments_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
