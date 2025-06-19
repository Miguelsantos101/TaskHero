package com.example.taskhero.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.taskhero.data.local.AppDatabase;
import com.example.taskhero.data.local.TaskDao;
import com.example.taskhero.data.model.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskRepository {

    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getTasksForUser(int userId) {
        return taskDao.getTasksForUser(userId);
    }

    public Future<Long> insertTask(Task task) {
        return executorService.submit(() -> taskDao.insert(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }

    public LiveData<Task> getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }
}