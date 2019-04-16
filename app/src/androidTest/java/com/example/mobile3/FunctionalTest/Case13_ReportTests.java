package com.example.mobile3.FunctionalTest;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.ProjectInfoActivity;
import com.example.mobile3.R;
import com.example.mobile3.ReportActivity;
import com.example.mobile3.UserListActivity;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Case13_ReportTests {
    static JSONObject mockProjectResponse;
    static JSONObject mockReportResponse;
    static String projectId;
    static int pomodorosCompleted = 10;
    static int  hoursWorked = 1;


    @BeforeClass
    public static void start() {
        projectId = "1";
        try {
            mockProjectResponse =  new JSONObject();
            mockProjectResponse.put("projectname", "test project");

            mockReportResponse = new JSONObject();
            mockReportResponse.put("sessions", "[" +
                    "{\"startingTime\": \"2019-02-18T20:00Z\"," +
                    "\"endingTime\": \"2019-02-20T20:00Z\"," +
                    "\"hoursWorked\": 5"+
                    "}," +
                    "{\"startingTime\": \"2019-02-21T20:00Z\"," +
                    "\"endingTime\": \"2019-02-24T20:00Z\"," +
                    "\"hoursWorked\": 8"+
                    "}]");
            mockReportResponse.put("completedPomodoros", pomodorosCompleted);
            mockReportResponse.put("totalHoursWorkedOnProject", hoursWorked);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Before
    public void init() {
        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockProjectResponse.toString())));
        Intents.init();
    }

    @After
    public void clean() {
        Intents.release();
    }


    /**
     * 13 Report Tests
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000)); // No-args constructor defaults to port 8080

    @Rule
    public final ActivityTestRule<ProjectInfoActivity> projectInfoActivityRule =
            new ActivityTestRule<>(ProjectInfoActivity.class, true, false);

    //13.1.1 No Pomodoros, No hours worked
    @Test
    public void reportNoPomNoHourTest() throws InterruptedException {
        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId+"/report?(.*)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockReportResponse.toString())));

        Log.e("@Test","13.1.1 Performing Generate Report with no pom and no hours.");
        Intent intent = new Intent();
        intent.putExtra("id", projectId);
        projectInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        Intents.intended(hasComponent((ReportActivity.class.getName())));
        sleep(500);
        onData(instanceOf(ReportSession.class))
                .inAdapterView(allOf(withId(R.id.reportSessionList), isDisplayed()))
                .atPosition(1)
                .check(matches(isDisplayed()));
        projectInfoActivityRule.finishActivity();
    }


    //13.1.2 No Pomodoros, Show hours worked
    @Test
    public void reportNoPomShowHourTest() throws InterruptedException {
        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId+"/report?(.*)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockReportResponse.toString())));


        Log.e("@Test","13.1.2 Performing Generate Report with no pom but with hours.");
        Intent intent = new Intent();
        intent.putExtra("id", projectId);
        projectInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.checkbox_includeHours)).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        Intents.intended(hasComponent((ReportActivity.class.getName())));
        sleep(500);
        onView(withId(R.id.totalHoursWorked)).check(matches(withText(Integer.toString(hoursWorked) + "h0m")));
        projectInfoActivityRule.finishActivity();
    }


    //13.2.1 Show Pomodoros, no hours worked
    @Test
    public void reportShowPomNoHourTest() throws InterruptedException {
        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId+"/report?(.*)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockReportResponse.toString())));

        Log.e("@Test","13.2.1 Performing Generate Report with pom but no hours.");
        Intent intent = new Intent();
        intent.putExtra("id", projectId);
        projectInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.checkbox_includePomodoros)).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        Intents.intended(hasComponent((ReportActivity.class.getName())));
        sleep(500);
        onView(withId(R.id.completedPomodoros)).check(matches(withText(Integer.toString(pomodorosCompleted))));
        projectInfoActivityRule.finishActivity();
    }


    //13.2.2 Show Pomodoros, Show hours worked
    @Test
    public void reportShowPomShowHourTest() throws InterruptedException {

        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId+"/report?(.*)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockReportResponse.toString())));

        Log.e("@Test","13.2.1 Performing Generate Report with pom and hours.");
        Intent intent = new Intent();
        intent.putExtra("id", projectId);
        projectInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.checkbox_includePomodoros)).perform(click());
        onView(withId(R.id.checkbox_includeHours)).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        Intents.intended(hasComponent((ReportActivity.class.getName())));
        sleep(500);
        onView(withId(R.id.totalHoursWorked)).check(matches(withText(Integer.toString(hoursWorked) + "h0m")));
        onView(withId(R.id.completedPomodoros)).check(matches(withText(Integer.toString(pomodorosCompleted))));
        projectInfoActivityRule.finishActivity();
    }

    //13.3 404 response
    @Test
    public void reportNotFoundTest() throws InterruptedException {

        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId + "/report?(.*)"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"status\":404}")));

        Log.e("@Test","13.3 Performing report not found test.");
        Intent intent = new Intent();
        intent.putExtra("id", projectId);
        projectInfoActivityRule.launchActivity(intent);
        onView(withId(R.id.reportButton)).perform(click());
        sleep(500);
        Intents.intended(hasComponent((ReportActivity.class.getName())));
        sleep(1000);
        onView(withText(R.string.error_report_not_found)).inRoot(withDecorView(not(projectInfoActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        projectInfoActivityRule.finishActivity();
    }

}



