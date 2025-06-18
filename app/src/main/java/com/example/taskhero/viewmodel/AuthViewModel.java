package com.example.taskhero.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskhero.R;
import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.TaskRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AuthViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final MutableLiveData<User> loginSuccessEvent = new MutableLiveData<>();
    private final MutableLiveData<String> loginErrorEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationResult = new MutableLiveData<>();
    private static final String TAG = "AuthViewModel";

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public LiveData<User> getLoginSuccessEvent() {
        return loginSuccessEvent;
    }

    public LiveData<String> getLoginErrorEvent() {
        return loginErrorEvent;
    }

    public LiveData<Boolean> getRegistrationResult() {
        return registrationResult;
    }

    public void loginUser(String email, String passwordHash) {
        new Thread(() -> {
            try {
                Future<User> future = repository.findByCredentials(email, passwordHash);
                User user = future.get();
                if (user != null) {
                    loginSuccessEvent.postValue(user);
                } else {
                    loginErrorEvent.postValue(getApplication().getString(R.string.login_error_invalid_credentials));
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error while trying to log in user", e);
                loginErrorEvent.postValue(getApplication().getString(R.string.login_error_generic));
            }
        }).start();
    }

    public void onLoginHandled() {
        loginSuccessEvent.setValue(null);
        loginErrorEvent.setValue(null);
    }

    public void registerUser(User user) {
        try {
            Future<User> future = repository.findByEmail(user.getEmail());
            User existingUser = future.get();

            if (existingUser == null) {
                repository.registerUser(user);
                registrationResult.postValue(true);
            } else {
                registrationResult.postValue(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error during registration check", e);
            registrationResult.postValue(false);
        }
    }

    public void onRegistrationHandled() {
        registrationResult.setValue(null);
    }
}