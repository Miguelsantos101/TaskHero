package com.example.taskhero;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.taskhero.R.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.taskhero.ui.auth.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Full UI test for the registration and login flow.
 */
@RunWith(AndroidJUnit4.class)
public class AuthFlowTest {

    private static final String TAG = "AuthFlowTest";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void fullRegistrationAndLoginFlow() {
        onView(withId(R.id.text_view_go_to_register)).perform(click());
        onView(withId(R.id.button_register)).check(matches(isDisplayed()));

        String uniqueEmail = "test@gmail.com";
        String password = "123";

        File imageDir = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir();
        File tempFile = new File(imageDir, "test_image.jpg");
        try {
            boolean newFile = tempFile.createNewFile();
            if (!newFile) {
                throw new IOException("Failed to create temporary image file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri imageUri = FileProvider.getUriForFile(InstrumentationRegistry.getInstrumentation().getTargetContext(), "com.example.taskhero.provider", tempFile);

        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

        onView(withId(R.id.image_view_profile)).perform(click());
        String[] options = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getStringArray(R.array.photo_source_options);
        onView(withText(options[1])).perform(click());

        onView(withId(R.id.edit_text_name)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_email_register)).perform(typeText(uniqueEmail), closeSoftKeyboard());
        onView(withId(R.id.edit_text_password_register)).perform(typeText(password), closeSoftKeyboard());


        onView(withId(R.id.button_register)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.button_login)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_text_email_login)).perform(typeText(uniqueEmail), closeSoftKeyboard());
        onView(withId(R.id.edit_text_password_login)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());
        allowSystemPermissionDialog();
        onView(withId(R.id.recycler_view_tasks)).check(matches(isDisplayed()));
    }

    private void allowSystemPermissionDialog() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = device.findObject(new UiSelector().text(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getString(string.testing_notification_allow_button)));

        try {
            if (allowButton.exists() && allowButton.isEnabled()) {
                try {
                    allowButton.click();
                } catch (Exception e) {
                    Log.e(TAG, "Could not click the Allow button", e);
                }
            }
        } catch (UiObjectNotFoundException e) {
            Log.e(TAG, "Permission dialog button not found", e);
        }
    }
}
