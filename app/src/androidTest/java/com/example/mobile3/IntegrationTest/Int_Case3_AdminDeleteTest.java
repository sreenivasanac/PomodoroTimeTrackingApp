package com.example.mobile3.IntegrationTest;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.LoginActivity;
import com.example.mobile3.ProjectInfoActivity;
import com.example.mobile3.R;
import com.example.mobile3.UserInfoActivity;
import com.example.mobile3.UserListActivity;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Int_Case3_AdminDeleteTest {
    String email = "case3test@mobile3.com";
    String firstName = "John";
    String lastName = "Doe";
    String projectName = "case3testProject";

    @Before
    public void init() {

        Intents.init();
    }

    @After
    public void clean() {

        Intents.release();
    }


    /**
     * 3 Admin delete user tests
     */

    @Rule
    public final ActivityTestRule<UserInfoActivity> userInfoActivityRule =
            new ActivityTestRule<>(UserInfoActivity.class, true, false);
    @Rule
    public final ActivityTestRule<UserListActivity> userListActivityRule =
            new ActivityTestRule<>(UserListActivity.class, true, false);

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivity =
            new ActivityTestRule<LoginActivity>(LoginActivity.class, true, false);


    //3.1 Admin delete non-exist user test unreachable?
//    @Test
//    public void adminDeleteNonExistUserTest() {
//        //currently must fail due to lack of implementation
//        Log.e("@Test","3.1 Performing admin delete non-exist user test");
//        Intent intent = new Intent();
//        intent.putExtra("id","999999999"); //retrieve an non exist user
//
//        userInfoActivityRule.launchActivity(intent);
//        onView(ViewMatchers.withId(R.id.deleteButton))
//                .perform(click());
//        SystemClock.sleep(1000);
//        onView(withText(R.string.error_user_not_found))
//                .inRoot(isDialog())
//                .check(matches(isDisplayed()));
//        userInfoActivityRule.finishActivity();
//
//    }

    //3.2 Admin delete existing user
    //3.2.1 Admin delete user without projects
    @Test
    public void adminDeleteExistingUserWithoutProjectTest() {
        Log.e("@Test","3.2.1 Performing admin delete user without project test");
        loginActivity.launchActivity(null); //confirmation of delete
        //login as admin
        onView(withId(R.id.adminLogin)).perform(click());
        //create user
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        //delete user
        sleep(500);
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        onView(withId(R.id.deleteButton)).perform(click());
        sleep(500);
        intended(hasComponent((UserListActivity.class.getName())),times(3));

        loginActivity.finishActivity();
    }

    //3.2.2 Admin delete user with projects
    //3.2.2.1 Admin delete user with projects and cancels
    //3.2.2.2 Admin delete user with projects and confirms
    @Test
    public void adminDeleteUserWithProject() {
        loginActivity.launchActivity(null); //confirmation of delete
        //login as admin
        sleep(500);
        onView(withId(R.id.adminLogin)).perform(click());
        //create user
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        //Logout, login as user
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        //create project
        onView(withId(R.id.viewProjectsButton)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        //Logout, login as admin
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.adminLogin)).perform(click());
        //delete user
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        onView(withId(R.id.deleteButton)).perform(click());
        //check message
        onView(withText(R.string.confirm_delete_user_with_project))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        //press cancel
        onView(withId(android.R.id.button2))
                .perform(click());
        //stay in project info
        sleep(500);
        intended(hasComponent((UserInfoActivity.class.getName())),times(1));
        //delete confirm
        onView(withId(R.id.deleteButton)).perform(click());
        //check message
        onView(withText(R.string.confirm_delete_user_with_project))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        //press confirm
        onView(withId(android.R.id.button1))
                .perform(click());
        sleep(500);
        intended(hasComponent((UserListActivity.class.getName())),times(4));


        //CLEANUP, ideally this should be from backend CASCADE
//        //remove projects
//
//        Espresso.pressBack();
//        //login as admin
//        onView(withId(R.id.adminLogin)).perform(click());
//        //create user
//        onView(withId(R.id.fab)).perform(click());
//        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
//        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
//        onView(withId(R.id.emailField)).perform(typeText(email), closeSoftKeyboard());
//        onView(withId(R.id.createButton)).perform(click());
//        //Logout, login as user
//        Espresso.pressBack();
//        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
//        onView(withId(R.id.userLogin)).perform(click());
//        //delete project
//        onData(hasToString(startsWith(projectName)))
//                .inAdapterView(withId(R.id.projectList))
//                .perform(click());
//        onData(withId(R.id.deleteButton)).perform(click());
//        //Logout, login as admin
//        Espresso.pressBack();
//        onView(withId(R.id.adminLogin)).perform(click());
//        //delete user
//        onData(hasToString(startsWith(email)))
//                .inAdapterView(withId(R.id.userList))
//                .perform(click());
//        onView(withId(R.id.deleteButton)).perform(click());

        loginActivity.finishActivity();
    }





}



