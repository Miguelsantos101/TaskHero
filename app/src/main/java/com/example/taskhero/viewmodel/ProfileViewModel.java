package com.example.taskhero.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.TaskRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public LiveData<User> getUser(int userId) {
        return repository.getUserById(userId);
    }
}