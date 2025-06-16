package com.example.taskhero.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskhero.R;
import com.example.taskhero.databinding.ActivityLoginBinding;
import com.example.taskhero.ui.main.MainActivity;
import com.example.taskhero.util.Constants;
import com.example.taskhero.util.NotificationScheduler;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);

        checkUserSession();

        NotificationScheduler.createNotificationChannel(this);

        // Restart database if it is corrupted
        // this.deleteDatabase("taskhero_database");

        com.example.taskhero.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new LoginFragment(), false);
        }
    }

    private void checkUserSession() {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(Constants.LOGGED_IN_USER_KEY, -1);

        if (userId != -1) {
            navigateToMain();
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}