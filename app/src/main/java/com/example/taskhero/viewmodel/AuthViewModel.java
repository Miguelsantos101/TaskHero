package com.example.taskhero.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.TaskRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AuthViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final MutableLiveData<User> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationResult = new MutableLiveData<>();
    private static final String TAG = "AuthViewModel";

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public LiveData<User> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getRegistrationResult() {
        return registrationResult;
    }

    public void loginUser(String email, String passwordHash) {
        try {
            Future<User> future = repository.findByCredentials(email, passwordHash);
            User user = future.get();
            loginResult.postValue(user);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error while trying to log in user", e);
            loginResult.postValue(null);
        }
    }

    public void registerUser(User user) {
        try {
            Future<User> future = repository.findByEmail(user.getEmail());
            if (future.get() == null) {
                repository.registerUser(user);
                registrationResult.postValue(true);
            } else {
                registrationResult.postValue(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error while trying to register user", e);
            registrationResult.postValue(false);
        }
    }

    public void onRegistrationHandled() {
        registrationResult.setValue(null);
    }
}