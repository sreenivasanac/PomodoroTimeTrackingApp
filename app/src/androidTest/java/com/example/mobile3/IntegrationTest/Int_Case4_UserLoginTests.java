package com.example.mobile3.IntegrationTest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.LoginActivity;
import com.example.mobile3.PomodoroSessionActivity;
import com.example.mobile3.ProjectListActivity;
import com.example.mobile3.R;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Int_Case4_UserLoginTests {
    String email = "case4test@mobile3.com";
    String firstName = "John";
    String lastName = "Doe";

    @Before
    public void init() {
        //add test user
        //UserMap.getInstance().create("TestFirstName", "TestLastName", "test@test.com");
        Intents.init();
    }

    @After
    public void clean() {
        Intents.release();
    }


    /**
     * 4 User Login Tests
     */

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    //4.1 Non-existing User Login
    @Test
    public void userLoginNonExistTest() {
        Log.e("@Test","4.1 Performing login user non-exist test");
        loginActivityRule.launchActivity(null);
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText(email));
        Espresso.onView(withId(R.id.userLogin))
                .perform(ViewActions.click());
        Espresso.onView(withId(R.id.email))
                .check(matches(hasErrorText("User does not exists!")));
        loginActivityRule.finishActivity();
    }

    //4.1.1 Bad email login
    @Test
    public void userLoginBadEmailTest() {
        Log.e("@Test","4.1.1 Performing login user bad email test");
        loginActivityRule.launchActivity(null);
        Espresso.onView(withId(R.id.email))
                .perform(ViewActions.typeText("bademail"));
        Espresso.onView(withId(R.id.userLogin))
                .perform(ViewActions.click());
        Espresso.onView(withId(R.id.email))
                .check(matches(hasErrorText("This email address is invalid")));
        loginActivityRule.finishActivity();

    }

    //4.2 Existing User Login
    @Test
    public void userLoginTest() {
        Log.e("@Test","4.2 Performing login user test");

        loginActivityRule.launchActivity(null);
        //create user
        onView(withId(R.id.adminLogin)).perform(click());
        //create user
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.firstNameField)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField)).perform(typeText(lastName), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        sleep(500);

        //test login
        Espresso.pressBack();
        sleep(500);
        Espresso.onView(withId(R.id.email))
                .perform(ViewActions.typeText(email));
        Espresso.onView(withId(R.id.userLogin))
                .perform(ViewActions.click());
        intended(hasComponent(PomodoroSessionActivity.class.getName()));
        sleep(500);

        //delete user
        Espresso.pressBack();
        sleep(500);
        Espresso.pressBack();
        sleep(500);
        onView(withId(R.id.adminLogin)).perform(click());
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        onView(withId(R.id.deleteButton)).perform(click());

        loginActivityRule.finishActivity();

    }




}



