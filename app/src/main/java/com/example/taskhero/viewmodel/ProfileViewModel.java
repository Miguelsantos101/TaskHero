package com.example.taskhero.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.UserRepository;

import java.util.concurrent.Future;

public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> updateError = new MutableLiveData<>();
    private static final String TAG = "ProfileViewModel";


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<User> getUser(int userId) {
        return userRepository.getUserById(userId);
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<String> getUpdateError() {
        return updateError;
    }

    public void updateUserProfile(User userToUpdate, String newName, String newEmail, String newPhotoUriString) {
        userToUpdate.setName(newName);
        if (newPhotoUriString != null) {
            userToUpdate.setPhotoUri(newPhotoUriString);
        }

        if (!userToUpdate.getEmail().equals(newEmail)) {
            try {
                Future<User> future = userRepository.findByEmail(newEmail);
                User existingUser = future.get();

                if (existingUser == null) {
                    userToUpdate.setEmail(newEmail);
                    userRepository.updateUser(userToUpdate);
                    updateSuccess.postValue(true);
                } else {
                    updateError.postValue(getApplication().getString(R.string.edit_profile_snackbar_error_email_exists));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking email", e);
                updateError.postValue(getApplication().getString(R.string.edit_profile_snackbar_error_verifying_email));
            }
        } else {
            userRepository.updateUser(userToUpdate);
            updateSuccess.postValue(true);
        }
    }

    public void onUpdateHandled() {
        updateSuccess.setValue(null);
        updateError.setValue(null);
    }
}