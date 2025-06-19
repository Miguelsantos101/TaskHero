package com.example.taskhero.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.taskhero.data.local.AppDatabase;
import com.example.taskhero.data.local.UserDao;
import com.example.taskhero.data.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
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

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }
}