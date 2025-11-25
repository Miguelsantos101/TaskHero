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

    public int completeTask(Task task, boolean isCompleted, User currentUser) {
        task.setCompleted(isCompleted);
        updateTask(task);

        if (!isCompleted || currentUser == null) {
            return 0;
        }

        int basePoints;
        switch (task.getDifficultyEnum()) {
            case EASY: basePoints = 5; break;
            case HARD: basePoints = 20; break;
            case MEDIUM:
            default: basePoints = 10; break;
        }

        int punctualityBonus = (task.getDueDate() > 0 && System.currentTimeMillis() <= task.getDueDate()) ? 5 : 0;

        long today = getStartOfDay(System.currentTimeMillis());
        long lastDay = getStartOfDay(currentUser.getLastCompletionDate());

        if (today > lastDay) {
            if (today - lastDay == (24 * 60 * 60 * 1000)) {
                currentUser.dailyStreak++;
            } else {
                currentUser.dailyStreak = 1;
            }
            currentUser.setLastCompletionDate(System.currentTimeMillis());
        }

        double streakMultiplier = 1.0 + (Math.min(currentUser.dailyStreak - 1, 10) * 0.1);
        int finalPoints = (int) ((basePoints + punctualityBonus) * streakMultiplier);
        
        currentUser.setScore(currentUser.getScore() + finalPoints);
        updateUser(currentUser);
        
        return finalPoints;
    }

    private long getStartOfDay(long timestamp) {
        if (timestamp == 0) return 0;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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