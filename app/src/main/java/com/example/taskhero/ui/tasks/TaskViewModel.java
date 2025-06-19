package com.example.taskhero.ui.tasks;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskhero.data.model.Task;
import com.example.taskhero.data.model.User;
import com.example.taskhero.data.repository.TaskRepository;
import com.example.taskhero.data.repository.UserRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<Long> newlyInsertedTaskId = new MutableLiveData<>();
    private static final String TAG = "TaskViewModel";



    public TaskViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        taskRepository = new TaskRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getTasksForUser(int userId) {
        return taskRepository.getTasksForUser(userId);
    }

    public void insertTask(Task task) {
        executorService.execute(() -> {
            try {
                Future<Long> future = taskRepository.insertTask(task);
                Long newId = future.get();
                newlyInsertedTaskId.postValue(newId);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting task and getting id", e);
                newlyInsertedTaskId.postValue(-1L);
            }
        });
    }

    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    public void deleteTask(Task task) {
        taskRepository.deleteTask(task);
    }

    public LiveData<Task> getTaskById(int taskId) {
        return taskRepository.getTaskById(taskId);
    }

    public LiveData<User> getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public LiveData<Long> getNewlyInsertedTaskId() {
        return newlyInsertedTaskId;
    }

    public void onNewTaskHandled() {
        newlyInsertedTaskId.setValue(null);
    }
}