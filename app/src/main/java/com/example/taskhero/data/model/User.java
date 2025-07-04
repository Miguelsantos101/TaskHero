package com.example.taskhero.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @ColumnInfo(name = "photo_uri")
    public String photoUri;

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "daily_streak", defaultValue = "0")
    public int dailyStreak;

    @ColumnInfo(name = "last_completion_date", defaultValue = "0")
    public long lastCompletionDate;

    @Ignore
    public User() {
    }

    public User(String name, String email, String passwordHash, String photoUri) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.photoUri = photoUri;
        this.score = 0;
        this.dailyStreak = 0;
        this.lastCompletionDate = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getPasswordHash() {
        return passwordHash;
    }

    @SuppressWarnings("unused")
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @SuppressWarnings("unused")
    public int getDailyStreak() {
        return dailyStreak;
    }

    @SuppressWarnings("unused")
    public void setDailyStreak(int dailyStreak) {
        this.dailyStreak = dailyStreak;
    }

    public long getLastCompletionDate() {
        return lastCompletionDate;
    }

    public void setLastCompletionDate(long lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }
}