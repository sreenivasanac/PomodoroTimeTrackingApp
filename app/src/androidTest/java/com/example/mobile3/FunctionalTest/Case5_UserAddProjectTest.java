package com.example.mobile3.FunctionalTest;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.AddProjectActivity;
import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.ProjectInfoActivity;
import com.example.mobile3.ProjectListActivity;
import com.example.mobile3.R;
import com.example.mobile3.ReportActivity;
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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Case5_UserAddProjectTest {
    static JSONObject mockProjectResponse;
    static int projectId = 1;
    static String projectName = "test project";
    static int userId = 1;

    @BeforeClass
    public static void start() {
        projectId = 1;

        try {
            mockProjectResponse =  new JSONObject();
            mockProjectResponse.put("id", projectId);
            mockProjectResponse.put("projectname", projectName);
            mockProjectResponse.put("userId", userId);

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
     * 5 User Add project test
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000)); // No-args constructor defaults to port 8080

    @Rule
    public final ActivityTestRule<ProjectInfoActivity> projectInfoActivityRule =
            new ActivityTestRule<>(ProjectInfoActivity.class, true, false);

    @Rule
    public final ActivityTestRule<AddProjectActivity> addProjectActivityRule =
            new ActivityTestRule<>(AddProjectActivity.class, true, false);

    //5.1 User Add project
    @Test
    public void addProjectTest() throws InterruptedException {
        wireMockRule.stubFor(post(urlMatching("/users/(.*)/projects"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockProjectResponse.toString())));

        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("["+mockProjectResponse.toString()+"]")));

        Log.e("@Test","5.1 Performing add project test.");
        addProjectActivityRule.launchActivity(null);
        onView(withId(R.id.projectNameField)).perform(typeText(projectName), closeSoftKeyboard());
        onView(withId(R.id.createButton)).perform(click());
        sleep(5000);
        Intents.intended(hasComponent((ProjectListActivity.class.getName())));
        sleep(500);
        onData(hasToString(startsWith(projectName)))
                .inAdapterView(withId(R.id.projectList))
                .check(matches(isDisplayed()));
        addProjectActivityRule.finishActivity();
    }


}



