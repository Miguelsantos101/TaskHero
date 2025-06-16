package com.example.taskhero.ui.auth;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.taskhero.viewmodel.AuthViewModel;

@SuppressWarnings("unused")
public class RegisterFragment extends BasePhotoFragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;
    private static final String TAG = "RegisterFragment";

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
        authViewModel.getRegistrationResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    binding.editTextName.setText("");
                    binding.editTextEmailRegister.setText("");
                    binding.editTextPasswordRegister.setText("");
                    binding.imageViewProfile.setImageResource(R.drawable.ic_add_a_photo);

                    UIUtils.showSuccessSnackbar(requireView(), getString(R.string.success_register));
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_register));
                }
                authViewModel.onRegistrationHandled();
            }
        });
    }

    private void registerUser() {
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editTextEmailRegister.getText().toString().trim();
        String password = binding.editTextPasswordRegister.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || selectedImageUri == null) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_all_fields_and_photo_required));
            return;
        }

        String passwordHash = HashUtils.sha256(password);

        Uri internalImageUri = saveImageToInternalStorage(selectedImageUri);

        if (internalImageUri == null) {
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_saving_image));
            return;
        }

        User user = new User(name, email, passwordHash, internalImageUri.toString());
        authViewModel.registerUser(user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}