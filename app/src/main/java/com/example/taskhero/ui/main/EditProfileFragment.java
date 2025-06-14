package com.example.taskhero.ui.main;

import android.net.Uri;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.databinding.FragmentEditProfileBinding;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.ProfileViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@SuppressWarnings("unused")
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private User currentUser;
    private Uri newImageUri;
    private static final String TAG = "EditProfileFragment";

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUri = uri;
                    binding.imageViewEditProfile.setImageURI(uri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        if (getArguments() != null) {
            int userId = getArguments().getInt("USER_ID", -1);
            if (userId != -1) {
                profileViewModel.getUser(userId).observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        currentUser = user;
                        populateUI();
                    }
                });
            }
        }

        binding.imageViewEditProfile.setOnClickListener(v -> getContent.launch("image/*"));
        binding.buttonSaveProfile.setOnClickListener(v -> saveProfileChanges());
        setupObservers();
    }

    private void populateUI() {
        binding.editTextNameEdit.setText(currentUser.getName());
        binding.editTextEmailEdit.setText(currentUser.getEmail());
        if (currentUser.getPhotoUri() != null) {
            Glide.with(this).load(Uri.parse(currentUser.getPhotoUri())).into(binding.imageViewEditProfile);
        }
    }

    private void saveProfileChanges() {
        if (currentUser == null || !isFormValid()) {
            return;
        }

        String photoUriToSave = getFinalPhotoUriString();

        if (photoUriToSave == null && newImageUri != null) {
            return;
        }

        String newName = Objects.requireNonNull(binding.editTextNameEdit.getText()).toString().trim();
        String newEmail = Objects.requireNonNull(binding.editTextEmailEdit.getText()).toString().trim();

        profileViewModel.updateUserProfile(currentUser, newName, newEmail, photoUriToSave);
    }

    private boolean isFormValid() {
        boolean nameIsValid = validateName();
        boolean emailIsValid = validateEmail();
        return nameIsValid & emailIsValid;
    }

    private boolean validateName() {
        String newName = Objects.requireNonNull(binding.editTextNameEdit.getText()).toString().trim();
        if (newName.isEmpty()) {
            binding.textInputLayoutNameEdit.setError(getString(R.string.error_name_required));
            return false;
        } else {
            binding.textInputLayoutNameEdit.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String newEmail = Objects.requireNonNull(binding.editTextEmailEdit.getText()).toString().trim();
        if (newEmail.isEmpty()) {
            binding.textInputLayoutEmailEdit.setError(getString(R.string.error_email_required));
            return false;
        } else {
            binding.textInputLayoutEmailEdit.setError(null);
            return true;
        }
    }

    @Nullable
    private String getFinalPhotoUriString() {
        if (newImageUri != null) {
            Uri internalUri = saveImageToInternalStorage(newImageUri);
            if (internalUri != null) {
                return internalUri.toString();
            } else {
                UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_saving_new_image));
                return null;
            }
        } else {
            return currentUser.getPhotoUri();
        }
    }

    @Nullable
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

    private void setupObservers() {
        profileViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                UIUtils.showSuccessSnackbar(requireView(), getString(R.string.success_profile_updated));
                requireActivity().getSupportFragmentManager().popBackStack();
                profileViewModel.onUpdateHandled();
            }
        });

        profileViewModel.getUpdateError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                UIUtils.showErrorSnackbar(requireView(), error);
                profileViewModel.onUpdateHandled();
            }
        });
    }
}