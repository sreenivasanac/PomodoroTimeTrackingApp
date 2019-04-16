package com.example.mobile3.FunctionalTest;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.R;
import com.example.mobile3.UserInfoActivity;
import com.example.mobile3.UserListActivity;
import com.github.tomakehurst.wiremock.junit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.IsNot.not;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Case3_AdminInstrumentedTest {
    String testEmailWithProject;
    String testEmailWithoutProject;
    String userIdWithProject;
    String userFirstNameWithoutProject = "FNWOP";
    String userLastNameWithoutProject = "LNWOP";
    String userFirstNameWithProject = "FNWP";
    String userLastNameWithProject = "FNWP";

    @Before
    public void init() {
        //UserMap.getInstance().getMap().clear();
//        //add test user
//        testEmailWithProject = "test1@test.com";
//        testEmailWithoutProject = "test2@test.com";
//
//
//        UserMap.getInstance().create(userFirstNameWithoutProject, userLastNameWithoutProject, testEmailWithoutProject);
//        UserMap.getInstance().create(userFirstNameWithProject, userLastNameWithProject, testEmailWithProject);
//
//        userIdWithProject = UserMap.getInstance().getMap().get(testEmailWithProject).getId();
        //ProjectMap.getInstance().create("TestProject", userIdWithProject);
        Intents.init();
    }

    @After
    public void clean() {
//        UserMap.getInstance().getMap().remove(testEmailWithoutProject);
//        UserMap.getInstance().getMap().remove(testEmailWithProject);
//        ProjectMap.getInstance().getMap().remove("TestProject");
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
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000)); // No-args constructor defaults to port 8080


    //3.1 Admin delete non-exist user test
    @Test
    public void adminDeleteNonExistUserTest() {
        //currently must fail due to lack of implementation
        Log.e("@Test","3.1 Performing admin delete non-exist user test");
        Intent intent = new Intent();
        intent.putExtra("id","123");
        wireMockRule.stubFor(get("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users/123/projects")
                .willReturn(okJson("[]")));


        wireMockRule.stubFor(delete("/users/123")
                .willReturn(aResponse().withStatus(404).withBody("User not found")));

        userInfoActivityRule.launchActivity(intent);
        onView(ViewMatchers.withId(R.id.deleteButton))
                .perform(ViewActions.click());
        sleep(1000);
        onView(withText(R.string.error_user_not_found))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        userInfoActivityRule.finishActivity();

    }

    //3.2 Admin delete existing user
    //3.2.1 Admin delete user without projects
    @Test
    public void adminDeleteExistingUserWithoutProjectTest() {
        Log.e("@Test","3.2.1 Performing admin delete user without project test");

        wireMockRule.stubFor(get("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users/123/projects")
                .willReturn(okJson("[]")));

        wireMockRule.stubFor(delete("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users")
                .willReturn(okJson("[]")));

        Intent intent = new Intent();
        intent.putExtra("id", "123");

        userInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.deleteButton))
                .perform(ViewActions.click());
        sleep(5000);
        intended(hasComponent((UserListActivity.class.getName())));
        sleep(500);

        userInfoActivityRule.finishActivity();

//        Espresso.onData(anything())
//                .inAdapterView(withId(R.id.userList))
//                .atPosition(0)
//                .onChildView(withId(R.id.email))
//                .check(matches(not(withText(testEmailWithoutProject))));

    }

    //3.2.2 Admin delete user with projects
    //3.2.2.1 Admin delete user with projects and cancels
    @Test
    public void adminDeleteUserWithProjectCancel() {
        Log.e("@Test","3.2.2.1 Performing admin delete user with project and cancel test");
        wireMockRule.stubFor(get("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users/123/projects")
                .willReturn(okJson("[{},{}]")));

        wireMockRule.stubFor(delete("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users")
                .willReturn(okJson("[]")));

        Intent intent = new Intent();
        intent.putExtra("id", "123");

        userInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.deleteButton))
                .perform(ViewActions.click());
        sleep(1000);
        onView(withText(R.string.confirm_delete_user_with_project))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

//        onView(withText(R.string.confirm_delete_user_with_project))
//                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button2))
                .perform(ViewActions.click());
        //Stays in current activity
        sleep(500);
        intended(hasComponent(UserInfoActivity.class.getName()));
        sleep(500);
//        //dialog gone test?
//        //back to list and test user still there.
//        userListActivityRule.launchActivity(null);
//        intended(hasComponent(UserListActivity.class.getName()));
//        Espresso.onData(anything())
//                .inAdapterView(withId(R.id.userList))
//                .atPosition(0)      //Assume only one left, the one did not delete.
//                .onChildView(withId(R.id.email))
//                .check(matches(withText(testEmailWithProject)));
        userInfoActivityRule.finishActivity();

    }

    //3.2.2.2 Admin delete user with projects and confirms
    @Test
    public void adminDeleteUserWithProjectConfirm() {
        Log.e("@Test","3.2.2.2 Performing admin delete user with project and confirm test");
        wireMockRule.stubFor(get("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users/123/projects")
                .willReturn(okJson("[{},{}]")));

        wireMockRule.stubFor(delete("/users/123")
                .willReturn(okJson("{\"id\":123, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }")));

        wireMockRule.stubFor(get("/users")
                .willReturn(okJson("[]")));

        Intent intent = new Intent();
        intent.putExtra("id", "123");

        userInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.deleteButton))
                .perform(ViewActions.click());
        sleep(1000);

        onView(withText(R.string.confirm_delete_user_with_project))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

//        onView(withText("The current user has projects associated with them. Do you still want to delete the user?"))
//                .check(matches(isDisplayed()));
        onView(withId(android.R.id.button1))
                .perform(ViewActions.click());
        sleep(1000);
        intended(hasComponent(UserListActivity.class.getName()));
        sleep(500);
//        //check user deleted in list view
//        Espresso.onData(anything())
//                .inAdapterView(withId(R.id.userList))
//                .atPosition(0)      //assume only the one with project left in list (consider better ways..) and it's not what we deleted
//                .onChildView(withId(R.id.email))
//                .check(matches(not(withText(testEmailWithProject))));
        userInfoActivityRule.finishActivity();

    }





}



