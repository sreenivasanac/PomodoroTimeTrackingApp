package com.example.mobile3.IntegrationTest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.mobile3.LoginActivity;
import com.example.mobile3.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;

import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class int_LogPartialPomodoroTest {

    private String userEmail = "Story8test@mobile3.com";
    private String projectName = "TestProjectName";
    private String firstName = "testFirstName";
    private String lastName = "testLastName";
    private String newProjectName = "newProjectName";

    @Before
    public void init() {
        Intents.init();
    }

    @After
    public void clean() {
        Intents.release();
    }

    @Rule
    public ActivityTestRule<LoginActivity> activityRule
            = new ActivityTestRule<>(LoginActivity.class,true, false);

    @Test
    public void logPartialPomodoro() {
        activityRule.launchActivity(null);
        sleep(1000);

        //create user
        onView(withId(R.id.adminLogin)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //login as user
        onView(ViewMatchers.withId(R.id.email)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        sleep(500);

        //add project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        sleep(500);
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        sleep(500);
        onView(withId(R.id.createButton)).perform(click());
        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //start pomodoro and associate a project
        onView(withId(R.id.startButton)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        sleep(5000);

        //record a partial pomodoro
        onView(withId(R.id.resetButton)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Successfully Recorded")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //delete user
        onView(withId(R.id.adminLogin)).perform(click());
        onData(hasToString(startsWith(userEmail)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        sleep(500);
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        onView(withId(android.R.id.button1))
                .perform(click());
        activityRule.finishActivity();
    }

    @Test
    public void notLogPartialPomodoro() {
        activityRule.launchActivity(null);
        sleep(1000);

        //create user
        onView(withId(R.id.adminLogin)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //login as user
        onView(ViewMatchers.withId(R.id.email)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        sleep(500);

        //add project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        sleep(500);
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        sleep(500);
        onView(withId(R.id.createButton)).perform(click());
        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //start pomodoro and associate a project
        onView(withId(R.id.startButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        sleep(5000);

        //record a partial pomodoro
        onView(withId(R.id.resetButton)).perform(click());
        onView(withText("Pomodoro Summary")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        sleep(1000);
        Espresso.pressBack();
        sleep(1000);

        //delete user
        onView(withId(R.id.adminLogin)).perform(click());
        onData(hasToString(startsWith(userEmail)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        sleep(500);
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        onView(withId(android.R.id.button1))
                .perform(click());
        activityRule.finishActivity();
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

