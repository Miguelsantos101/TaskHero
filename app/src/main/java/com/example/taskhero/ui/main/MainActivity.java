package com.example.taskhero.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.taskhero.R;
import com.example.taskhero.databinding.ActivityMainBinding;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);

        com.example.taskhero.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        binding.bottomNavigation.setOnItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_tasks);
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack(null).commit();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_tasks) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new TaskListFragment()).commit();
            return true;
        } else if (itemId == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new ProfileFragment()).commit();
            return true;
        }

        return false;
    }
}