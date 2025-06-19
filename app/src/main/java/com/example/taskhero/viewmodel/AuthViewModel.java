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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final MutableLiveData<User> loginSuccessEvent = new MutableLiveData<>();
    private final MutableLiveData<String> loginErrorEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccessEvent = new MutableLiveData<>();
    private final MutableLiveData<String> registrationErrorEvent = new MutableLiveData<>();

    private static final String TAG = "AuthViewModel";

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<User> getLoginSuccessEvent() {
        return loginSuccessEvent;
    }

    public LiveData<String> getLoginErrorEvent() {
        return loginErrorEvent;
    }

    public LiveData<Boolean> getRegistrationSuccessEvent() {
        return registrationSuccessEvent;
    }

    public LiveData<String> getRegistrationErrorEvent() {
        return registrationErrorEvent;
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
        new Thread(() -> {
            try {
                Future<User> future = repository.findByEmail(user.getEmail());
                User existingUser = future.get();

                if (existingUser == null) {
                    repository.registerUser(user);
                    registrationSuccessEvent.postValue(true);
                } else {
                    registrationErrorEvent.postValue(getApplication().getString(R.string.register_error_email_exists));
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error during registration check", e);
                registrationErrorEvent.postValue(getApplication().getString(R.string.register_error_generic));
            }
        }).start();
    }

    public void onRegistrationHandled() {
        registrationSuccessEvent.setValue(null);
        registrationErrorEvent.setValue(null);
    }
}