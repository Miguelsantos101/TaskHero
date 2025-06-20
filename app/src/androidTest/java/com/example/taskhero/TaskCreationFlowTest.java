package com.example.taskhero;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.taskhero.ui.auth.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TaskCreationFlowTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void createFiveTasksWithDifferentDifficulties() throws InterruptedException {
        onView(withId(R.id.recycler_view_tasks)).check(matches(isDisplayed()));

        int[] difficulties = {R.id.radio_button_easy, R.id.radio_button_easy, R.id.radio_button_medium, R.id.radio_button_hard, R.id.radio_button_hard};

        for (int i = 1; i <= 5; i++) {
            onView(withId(R.id.fab_add_task)).perform(click());

            onView(withId(R.id.edit_text_task_title)).perform(typeText(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getString(R.string.task_form_add_title) + " " + i), closeSoftKeyboard());

            onView(withId(difficulties[i - 1])).perform(click());

            onView(withId(R.id.button_save_task)).perform(click());

            Thread.sleep(1000);
        }

        onView(withText(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getString(R.string.task_form_add_title) + " " + "5")).check(matches(isDisplayed()));
    }

}