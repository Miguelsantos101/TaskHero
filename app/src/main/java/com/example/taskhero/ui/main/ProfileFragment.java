package com.example.taskhero.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.taskhero.R;
import com.example.taskhero.databinding.FragmentProfileBinding;
import com.example.taskhero.ui.auth.LoginActivity;
import com.example.taskhero.viewmodel.ProfileViewModel;

@SuppressWarnings("unused")
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        if (userId != -1) {
            profileViewModel.getUser(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    binding.profileName.setText(user.getName());
                    binding.profileEmail.setText(user.getEmail());
                    binding.profileScore.setText(getString(R.string.profile_score, user.getScore()));
                    if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
                        binding.profileImage.setImageTintList(null);
                        binding.profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(this)
                                .load(Uri.parse(user.getPhotoUri()))
                                .into(binding.profileImage);
                    }
                }
            });
        }

        binding.buttonLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("TaskHeroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("LOGGED_IN_USER_ID");
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}