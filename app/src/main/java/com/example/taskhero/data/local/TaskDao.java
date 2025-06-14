package com.example.taskhero.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskhero.data.model.Task;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY due_date ASC")
    LiveData<List<Task>> getTasksForUser(int userId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(int taskId);
}