package com.example.taskhero.util;

import android.util.Patterns;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;

public class ValidationUtils {

    public static boolean validateRequiredField(EditText editText, TextInputLayout textInputLayout, String errorMessage) {
        String text = "";
        if (editText.getText() != null) {
            text = editText.getText().toString().trim();
        }

        if (text.isEmpty()) {
            textInputLayout.setError(errorMessage);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validateEmail(EditText editText, TextInputLayout textInputLayout, String requiredMessage, String invalidMessage) {
        String email = "";
        if (editText.getText() != null) {
            email = editText.getText().toString().trim();
        }

        if (email.isEmpty()) {
            textInputLayout.setError(requiredMessage);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayout.setError(invalidMessage);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
}