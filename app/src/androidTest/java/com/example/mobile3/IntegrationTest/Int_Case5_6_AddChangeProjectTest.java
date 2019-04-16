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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;

import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class Int_Case5_6_AddChangeProjectTest {

    private String userEmail = "test@test.com";
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
            = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void addProject() {
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
        //click add project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        sleep(500);
        //enter new project info
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        sleep(500);
        //click add
        onView(withId(R.id.createButton)).perform(click());
        sleep(500);
        //open the project just created
        onData(anything()).inAdapterView(withId(R.id.projectList)).atPosition(0).perform(click());
        sleep(500);
        //check if the project is added with the correct project name
        onView(withId(R.id.projectNameField)).check(matches(withText(projectName)));
        sleep(500);
        //go back to project list
        pressBack();
        sleep(500);
        //add a project with the same name
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        sleep(500);
        onView(withText("Project name existed!")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        //delete user
        Espresso.pressBack();
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.adminLogin)).perform(click());
        onData(hasToString(startsWith(userEmail)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        sleep(500);
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    @Test
    public void updateProject() {
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
        onView(withId(R.id.email)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        sleep(500);
        //click add project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        sleep(500);
        //enter new project info
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        sleep(500);
        //click add
        onView(withId(R.id.createButton)).perform(click());
        sleep(500);
        //open the project just created
        onData(anything()).inAdapterView(withId(R.id.projectList)).atPosition(0).perform(click());
        sleep(500);
        //enter new project name
        onView(withId(R.id.projectNameField)).perform(replaceText(newProjectName), closeSoftKeyboard());
        sleep(500);
        //click update
        onView(withId(R.id.updateButton)).perform(click());
        sleep(500);
        //open project again
        onData(anything()).inAdapterView(withId(R.id.projectList)).atPosition(0).perform(click());
        sleep(500);
        //check whether the project name is correctly updated
        onView(withId(R.id.projectNameField)).check(matches(withText(newProjectName)));
        sleep(5000);
        Espresso.pressBack();
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.adminLogin)).perform(click());
        onData(hasToString(startsWith(userEmail)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        sleep(500);
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
