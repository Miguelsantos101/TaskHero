package com.example.taskhero.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "tasks", foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE))
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id", index = true)
    public int userId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "due_date")
    public long dueDate;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "difficulty")
    public int difficulty;

    @Ignore
    public Task() {
    }

    public Task(int userId, String title, String description, long dueDate, int difficulty) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public int getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}