package com.example.taskhero.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.taskhero.data.local.AppDatabase;
import com.example.taskhero.data.local.TaskDao;
import com.example.taskhero.data.local.UserDao;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.data.model.User;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskRepository {

    private final UserDao userDao;
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void registerUser(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void updateUser(User user) {
        executorService.execute(() -> userDao.updateUser(user));
    }

    public Future<User> findByCredentials(String email, String passwordHash) {
        return executorService.submit(() -> userDao.findByCredentials(email, passwordHash));
    }

    public Future<User> findByEmail(String email) {
        return executorService.submit(() -> userDao.findByEmail(email));
    }

    public LiveData<List<Task>> getTasksForUser(int userId) {
        return taskDao.getTasksForUser(userId);
    }

    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public LiveData<Task> getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }
}