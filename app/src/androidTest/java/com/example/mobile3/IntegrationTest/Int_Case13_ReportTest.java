package com.example.mobile3.IntegrationTest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.LoginActivity;
import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.PomodoroSessionActivity;
import com.example.mobile3.ProjectListActivity;
import com.example.mobile3.R;
import com.example.mobile3.ReportActivity;
import com.example.mobile3.UserInfoActivity;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.AllOf.allOf;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Int_Case13_ReportTest {
    static String email = "case13test@mobile3.com";
    static String firstName = "John";
    static String lastName = "Doe";
    static String projectName = "testProject";


    @BeforeClass
    public static void start() {
        //admin login and add user
        classLoginRule.launchActivity(null);
        Espresso.onView(withId(R.id.adminLogin)).perform(click());
        sleep(5000);
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
        sleep(500);
        Espresso.pressBack();   //enter pomodoro page
        sleep(500);
        onView(withId(R.id.startButton)).perform(click());
        //confirm add project
        onView(withId(android.R.id.button1)).perform(click());
        //default selected project
        onView(withId(android.R.id.button1)).perform(click());
        sleep(500);
        onView(withId(R.id.resetButton)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        classLoginRule.finishActivity();
    }

    @AfterClass
    public static void end() {
        //admin login and add user
        classLoginRule.launchActivity(null);
        Espresso.onView(withId(R.id.adminLogin)).perform(click());
        sleep(500);
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




    @Before
    public void init() {
        Intents.init();
    }

    @After
    public void clean() {
        Intents.release();
    }


    /**
     * 13 Report Tests
     */

    @ClassRule
    public final static ActivityTestRule<LoginActivity> classLoginRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);


    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    //13.1.1 No pomodoro no timeworked tests
    @Test
    public void reportNoPomodoroNoTimeWorkedTest() {
        loginActivityRule.launchActivity(null);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        sleep(1000);
        onView(withId(R.id.viewProjectsButton)).perform(click());
        //view project info
        onData(hasToString(startsWith(projectName)))
                .inAdapterView(withId(R.id.projectList))
                .perform(click());
        //view report
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        intended(hasComponent((ReportActivity.class.getName())),times(1));
        sleep(500);
        //check these not exists
        onView(withId(R.id.ll_hoursWorked)).check(doesNotExist());
        onView(withId(R.id.ll_completePomodoros)).check(doesNotExist());
        //check at least one entry in report
        sleep(500);
        onData(instanceOf(ReportSession.class))
                .inAdapterView(allOf(withId(R.id.reportSessionList), isDisplayed()))
                .atPosition(0)
                .check(matches(isDisplayed()));

        loginActivityRule.finishActivity();
    }


    //13.2.2 show pomodoro show timeworked tests
    @Test
    public void reportShowPomodoroShowTimeWorkedTest() {
        loginActivityRule.launchActivity(null);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.userLogin)).perform(click());
        sleep(1000);
        onView(withId(R.id.viewProjectsButton)).perform(click());
        //view project info
        onData(hasToString(startsWith(projectName)))
                .inAdapterView(withId(R.id.projectList))
                .perform(click());
        //view report
        onView(withId(R.id.checkbox_includePomodoros)).perform(click());
        onView(withId(R.id.checkbox_includeHours)).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        intended(hasComponent((ReportActivity.class.getName())),times(1));
        sleep(500);
        //check these not exists
        onView(withId(R.id.ll_hoursWorked)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_completePomodoros)).check(matches(isDisplayed()));
        //check at least one entry in report
        sleep(500);
        onData(instanceOf(ReportSession.class))
                .inAdapterView(allOf(withId(R.id.reportSessionList), isDisplayed()))
                .atPosition(0)
                .check(matches(isDisplayed()));

        loginActivityRule.finishActivity();
    }


}



