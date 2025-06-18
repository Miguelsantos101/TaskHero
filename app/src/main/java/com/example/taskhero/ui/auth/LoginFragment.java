package com.example.taskhero.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.databinding.FragmentLoginBinding;
import com.example.taskhero.util.Constants;
import com.example.taskhero.util.HashUtils;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.buttonLogin.setOnClickListener(v -> handleLogin());

        binding.textViewGoToRegister.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).loadFragment(new RegisterFragment(), true);
            }
        });
    }

    private void observeViewModel() {
        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                UIUtils.showSuccessSnackbar(requireView(), getString(R.string.login_success_message));

                saveUserSession(user.getId());

                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).navigateToMain();
                }
            } else {
                UIUtils.showErrorSnackbar(requireView(), getString(R.string.login_error_invalid_credentials));
            }
        });
    }

    private void handleLogin() {
        String email = binding.editTextEmailLogin.getText().toString().trim();
        String password = binding.editTextPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.login_error_all_fields_required));
            return;
        }

        String passwordHash = HashUtils.sha256(password);
        authViewModel.loginUser(email, passwordHash);
    }

    private void saveUserSession(int userId) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.LOGGED_IN_USER_KEY, userId);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}