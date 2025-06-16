package com.example.taskhero.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskhero.R;
import com.example.taskhero.databinding.ActivityMainBinding;
import com.example.taskhero.util.UIUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            UIUtils.showErrorSnackbar(binding.getRoot(), getString(R.string.error_no_notification_permission));
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        askNotificationPermission();
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        binding.bottomNavigation.setOnItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_tasks);
        }
    }

    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_tasks) {
            loadFragment(new TaskListFragment(), false);
            return true;
        } else if (itemId == R.id.nav_profile) {
            loadFragment(new ProfileFragment(), false);
            return true;
        }

        return false;
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}