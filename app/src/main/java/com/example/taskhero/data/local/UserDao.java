package com.example.taskhero.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskhero.data.model.User;

@SuppressWarnings("unused")
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    User findByCredentials(String email, String passwordHash);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);
}