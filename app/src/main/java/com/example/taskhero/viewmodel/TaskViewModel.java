package com.example.taskhero.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.TaskRepository;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public LiveData<List<Task>> getTasksForUser(int userId) {
        return repository.getTasksForUser(userId);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public LiveData<Task> getTaskById(int taskId) {
        return repository.getTaskById(taskId);
    }

    public LiveData<User> getUserById(int userId) {
        return repository.getUserById(userId);
    }

    public void updateUser(User user) {
        repository.updateUser(user);
    }
}