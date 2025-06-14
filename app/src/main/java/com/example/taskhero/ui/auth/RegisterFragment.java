package com.example.taskhero.ui.auth;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.databinding.FragmentRegisterBinding;
import com.example.taskhero.util.HashUtils;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.AuthViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;
    private Uri selectedImageUri;
    private static final String TAG = "RegisterFragment";

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.imageViewProfile.setImageURI(uri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
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
        binding.imageViewProfile.setOnClickListener(v -> getContent.launch("image/*"));

        binding.buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void observeViewModel() {
        authViewModel.getRegistrationResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
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

        binding.editTextName.setText("");
        binding.editTextEmailRegister.setText("");
        binding.editTextPasswordRegister.setText("");
        binding.imageViewProfile.setImageURI(null);

        authViewModel.registerUser(user);
    }

    private Uri saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getFilesDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }

            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file);

        } catch (IOException e) {
            Log.e(TAG, "Error saving image to internal storage", e);
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}