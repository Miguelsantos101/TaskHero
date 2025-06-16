package com.example.taskhero.ui.auth;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.databinding.FragmentRegisterBinding;
import com.example.taskhero.util.HashUtils;
import com.example.taskhero.util.UIUtils;
import com.example.taskhero.viewmodel.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;
    private Uri tempImageUri;
    private Uri selectedImageUri;
    private static final String TAG = "RegisterFragment";

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                launchCamera();
            } else {
                UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_no_camera_permission));
            }
        });
    }

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
        if (success) {
            selectedImageUri = tempImageUri;
            binding.imageViewProfile.setImageURI(selectedImageUri);
        }
    });

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            selectedImageUri = uri;
            binding.imageViewProfile.setImageURI(selectedImageUri);
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        binding.imageViewProfile.setOnClickListener(v -> showPhotoSourceDialog());
        binding.buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void showPhotoSourceDialog() {


        final CharSequence[] options = getResources().getStringArray(R.array.photo_source_options);

        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.dialog_title_photo_source).setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkPermissionAndTakePhoto();
            } else {
                galleryLauncher.launch("image/*");
            }
        }).show();
    }

    private void checkPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File tempFile = File.createTempFile("JPEG_" + System.currentTimeMillis(), ".jpg", requireContext().getCacheDir());
            tempImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", tempFile);
            cameraLauncher.launch(tempImageUri);
        } catch (IOException e) {
            Log.e(TAG, "Error creating temp file for camera", e);
            UIUtils.showErrorSnackbar(requireView(), getString(R.string.error_preparing_camera));
        }
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