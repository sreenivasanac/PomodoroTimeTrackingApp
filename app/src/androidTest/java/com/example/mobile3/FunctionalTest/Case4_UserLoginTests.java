package com.example.mobile3.FunctionalTest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;

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

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Case4_UserLoginTests {

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
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000)); // No-args constructor defaults to port 8080

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    //4.1 Non-existing User Login
    @Test
    public void userLoginNonExistTest() {
        wireMockRule.stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                .withBody("[]")));

        Log.e("@Test","4.1 Performing login user non-exist test");
        loginActivityRule.launchActivity(null);
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("nonexist@email.com"));
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
        wireMockRule.stubFor(get("/users")
                .willReturn(okJson("[{\"id\":12345678, \"firstName\":\"john\", \"lastName\":\"doe\", \"email\":\"test@gatech.edu\" }]")));

        wireMockRule.stubFor(get("/users/12345678/projects")
                .willReturn(okJson("[]")));

        loginActivityRule.launchActivity(null);
        Espresso.onView(withId(R.id.email))
                .perform(ViewActions.typeText("test@gatech.edu"));
        Espresso.onView(withId(R.id.userLogin))
                .perform(ViewActions.click());
        intended(hasComponent(PomodoroSessionActivity.class.getName()));
        loginActivityRule.finishActivity();


    }




}



