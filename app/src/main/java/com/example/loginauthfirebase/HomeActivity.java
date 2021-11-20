package com.example.loginauthfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ClipData;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private MapsFragment mapsFragment;
    private ProfileFragment profileFragment;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Initialize();
    }

    private void Initialize() {
        prepareNavigationBar();
        prepareMapsFragment();
        prepareProfileFragment();
    }

    private void prepareMapsFragment() {
        mapsFragment = new MapsFragment();
        loadFragment(mapsFragment);
    }

    void prepareProfileFragment() {
        profileFragment = new ProfileFragment();
        loadFragment(profileFragment);
    }

    private void prepareNavigationBar() {
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.mapNavigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        if (item.getItemId() == navigationView.getSelectedItemId())
            return true;

        switch (item.getItemId()) {
            case R.id.profileNavigation:
                loadFragment(profileFragment);
                return true;

            case R.id.mapNavigation:
                loadFragment(mapsFragment);
                return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}