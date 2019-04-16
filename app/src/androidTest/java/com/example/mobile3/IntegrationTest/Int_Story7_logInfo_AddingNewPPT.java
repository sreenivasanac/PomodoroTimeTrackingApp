package com.example.mobile3.IntegrationTest;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.view.View;
import android.widget.ListView;

import com.example.mobile3.LoginActivity;
import com.example.mobile3.R;
import com.example.mobile3.UserListActivity;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import java.util.UUID;

import static android.app.PendingIntent.getActivity;
import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.EnumSet.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;

public class Int_Story7_logInfo_AddingNewPPT {

    static String email = "story7test@mobile3.com";
    static String firstName = "John";
    static String lastName = "Doe";
    static String projectName = "testProject";

    @BeforeClass
    public static void start() {
        //create user
        classLoginRule.launchActivity(null);
        onView(withId(R.id.adminLogin)).perform(click());
        //create user
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        //create project
        //Logout, login as user
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        //create project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        sleep(1000);
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        //create session
        classLoginRule.finishActivity();

    }

    @AfterClass
    public static void end() {
        //admin login and add user
        classLoginRule.launchActivity(null);
        Espresso.onView(withId(R.id.adminLogin)).perform(click());
        //delete user created
        //delete user
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        onView(withId(android.R.id.button1))
                .perform(click());
        classLoginRule.finishActivity();
    }

    @ClassRule
    public final static ActivityTestRule<LoginActivity> classLoginRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Rule
    public ActivityTestRule<LoginActivity> activityRule
            = new ActivityTestRule<>(LoginActivity.class, true, false);
    @Test
    public void whenOnlyFinishOnePomodoro_SummaryIsShown() {
        //testing values
//        String email =  "ss@jj.com";
        // Login as a user
        activityRule.launchActivity(null);
        sleep(500);
        onView(ViewMatchers.withId(R.id.email))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        onView(withId(R.id.startButton)).perform(click());
        sleep(500);
        onView(withText("YES")).perform(click());
        sleep(500);
        onView(withText("OK")).perform(click());
        sleep(10000);
        //check if the another Pomodoro dialog is shown
        onView(withText("Another Pomodoro?")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("NO")).perform(click());
        sleep(500);
        //check if the Pomodoro Summary is shown
        onView(withText("Pomodoro Summary")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        activityRule.finishActivity();


    }
    @Test
    public void whenAskSecondFinishOnePomodoro_StratsAnothorPomodoro() {
        //testing values
        //String email =  "ss@jj.com";
        // Login as a user
        activityRule.launchActivity(null);
        sleep(500);
        onView(ViewMatchers.withId(R.id.email))
                .perform(typeText(email), closeSoftKeyboard());
        sleep(500);
        onView(withId(R.id.userLogin)).perform(click());
        onView(withId(R.id.startButton)).perform(click());
        sleep(500);
        onView(withText("YES")).perform(click());
        sleep(500);
        onView(withText("OK")).perform(click());
        sleep(10000);
        //check if the another Pomodoro dialog is shown
        onView(withText("Another Pomodoro?")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("YES")).perform(click());
        sleep(10000);
        //check if the another Pomodoro dialog is shown
        onView(withText("Another Pomodoro?")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("NO")).perform(click());
        sleep(500);
        //check if the Pomodoro Summary is shown
        onView(withText("Pomodoro Summary")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        activityRule.finishActivity();

    }
}
