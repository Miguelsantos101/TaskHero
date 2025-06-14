package com.example.taskhero.util;

import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.taskhero.R;
import com.google.android.material.snackbar.Snackbar;

final public class UIUtils {

    public static void showErrorSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(view.getContext(), R.color.status_error));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSuccessSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(view.getContext(), R.color.status_success));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}