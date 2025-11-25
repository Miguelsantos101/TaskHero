package com.example.taskhero.ui.auth;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.databinding.FragmentRegisterBinding;
import com.example.taskhero.ui.base.BasePhotoFragment;
import com.example.taskhero.util.HashUtils;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.util.ValidationUtils;

import java.util.Objects;

public class RegisterFragment extends BasePhotoFragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.imageViewProfile.setOnClickListener(v -> showPhotoSourceDialog());
        binding.buttonRegister.setOnClickListener(v -> registerUser());

        observeViewModel();
    }

    @Override
    protected void onPhotoSelected(Uri uri) {
        binding.imageViewProfile.setImageURI(uri);
    }

    private void observeViewModel() {
        authViewModel.getRegistrationSuccessEvent().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                binding.editTextName.setText("");
                binding.editTextEmailRegister.setText("");
                binding.editTextPasswordRegister.setText("");
                binding.imageViewProfile.setImageResource(R.drawable.ic_add_a_photo);

                UIUtils.showSuccessSnackbar(requireView(), getString(R.string.register_snackbar_success));
                requireActivity().getSupportFragmentManager().popBackStack();

                authViewModel.onRegistrationHandled();
            }
        });

        authViewModel.getRegistrationErrorEvent().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                UIUtils.showErrorSnackbar(requireView(), errorMessage);
                authViewModel.onRegistrationHandled();
            }
        });
    }

    private boolean isFormValid() {
        boolean nameIsValid = ValidationUtils.validateRequiredField(
                binding.editTextName,
                binding.textInputLayoutName,
                getString(R.string.register_error_name_required)
        );

        boolean emailIsValid = ValidationUtils.validateEmail(
                binding.editTextEmailRegister,
                binding.textInputLayoutEmailRegister,
                getString(R.string.register_error_email_required),
                getString(R.string.register_error_invalid_email)
        );

        boolean passwordIsValid = validatePassword();
        return nameIsValid & emailIsValid & passwordIsValid;
    }

    private boolean validatePassword() {
        String password = Objects.requireNonNull(binding.editTextPasswordRegister.getText()).toString().trim();
        if (password.isEmpty()) {
            binding.textInputLayoutPasswordRegister.setError(getString(R.string.register_error_password_required));
            return false;
        } else if (password.length() < 3) {
            binding.textInputLayoutPasswordRegister.setError(getString(R.string.register_error_password_too_short));
            return false;
        } else {
            binding.textInputLayoutPasswordRegister.setError(null);
            return true;
        }
    }


    private void registerUser() {
        if (!isFormValid()) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.register_error_all_fields_required));
            return;
        }

        if (selectedImageUri == null) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.register_error_all_fields_required));
            return;
        }

        String name = Objects.requireNonNull(binding.editTextName.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.editTextEmailRegister.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.editTextPasswordRegister.getText()).toString().trim();

        Uri internalImageUri = saveImageToInternalStorage(selectedImageUri);
        if (internalImageUri == null) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.photo_error_saving_image));
            return;
        }

        String passwordHash = HashUtils.sha256(password);
        User user = new User(name, email, passwordHash, internalImageUri.toString());
        authViewModel.registerUser(user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}