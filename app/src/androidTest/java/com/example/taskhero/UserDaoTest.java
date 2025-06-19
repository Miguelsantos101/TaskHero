package com.example.taskhero;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.taskhero.data.local.AppDatabase;
import com.example.taskhero.data.local.UserDao;
import com.example.taskhero.data.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration test for UserDao.
 * Verifies write and read operations in the Room database.
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    private UserDao userDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = db.userDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndFindUserByEmail() {
        User user = new User("John Doe", "john.doe@example.com", "hash123", "uri_to_photo");

        userDao.insert(user);

        User foundUser = userDao.findByEmail("john.doe@example.com");

        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
    }
}