package com.example.taskhero.ui.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.taskhero.R;
import com.example.taskhero.util.UIUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class BasePhotoFragment extends Fragment {

    private static final String TAG = "BasePhotoFragment";
    protected Uri tempImageUri;
    protected Uri selectedImageUri;

    protected ActivityResultLauncher<String> requestPermissionLauncher;
    protected ActivityResultLauncher<Uri> cameraLauncher;
    protected ActivityResultLauncher<String> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        if (getView() != null)
                            UIUtils.showErrorSnackbar(getView(), getString(R.string.error_no_camera_permission));
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (success) {
                        selectedImageUri = tempImageUri;
                        onPhotoSelected(selectedImageUri);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        onPhotoSelected(selectedImageUri);
                    }
                });
    }

    protected void showPhotoSourceDialog() {
        final CharSequence[] options = getResources().getStringArray(R.array.photo_source_options);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_title_photo_source)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkPermissionAndTakePhoto();
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
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
            if (getView() != null)
                UIUtils.showErrorSnackbar(getView(), getString(R.string.error_preparing_camera));
        }
    }

    @Nullable
    protected Uri saveImageToInternalStorage(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return null;

            String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getFilesDir(), fileName);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
            }
            return Uri.fromFile(file);
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to internal storage", e);
            return null;
        }
    }

    protected abstract void onPhotoSelected(Uri uri);
}