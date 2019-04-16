package com.example.mobile3.FunctionalTest;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.mobile3.AddProjectActivity;
import com.example.mobile3.ProjectInfoActivity;
import com.example.mobile3.ProjectListActivity;
import com.example.mobile3.R;
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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Case6_UserUpdateProjectTest {
    static JSONObject mockProjectResponse;
    static JSONObject mockUpdatedProjectResponse;
    static int projectId = 1;
    static String projectName = "test project";
    static String newProjectName = "new test project";
    static int userId = 1;

    @BeforeClass
    public static void start() {
        projectId = 1;

        try {
            mockProjectResponse =  new JSONObject();
            mockProjectResponse.put("id", projectId);
            mockProjectResponse.put("projectname", projectName);
            mockProjectResponse.put("userId", userId);

            mockUpdatedProjectResponse = new JSONObject();
            mockUpdatedProjectResponse.put("id", projectId);
            mockUpdatedProjectResponse.put("projectname", newProjectName);
            mockUpdatedProjectResponse.put("userId", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Before
    public void init() {
        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId))
                .inScenario("Update")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockProjectResponse.toString())));

        wireMockRule.stubFor(put(urlMatching("/users/(.*)/projects/"+projectId))
                .inScenario("Update")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockUpdatedProjectResponse.toString()))
                .willSetStateTo("Updated Project Name"));

        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects"))
                .inScenario("Update")
                .whenScenarioStateIs("Updated Project Name")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("["+mockUpdatedProjectResponse.toString()+"]")));

        wireMockRule.stubFor(get(urlMatching("/users/(.*)/projects/"+projectId))
                .inScenario("Update")
                .whenScenarioStateIs("Updated Project Name")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mockUpdatedProjectResponse.toString())));


        Intents.init();
    }

    @After
    public void clean() {
        Intents.release();
    }


    /**
     * 6 User Update project test
     */
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000)); // No-args constructor defaults to port 8080

    @Rule
    public final ActivityTestRule<ProjectInfoActivity> projectInfoActivityRule =
            new ActivityTestRule<>(ProjectInfoActivity.class, true, false);

    @Rule
    public final ActivityTestRule<AddProjectActivity> addProjectActivityRule =
            new ActivityTestRule<>(AddProjectActivity.class, true, false);

    //6.1 User update existing project
    //6.1.1 User update project name unique
    @Test
    public void updateProjectTest() throws InterruptedException {

        Log.e("@Test","6.1.1 Performing user update project test.");
        Intent intent = new Intent();
        intent.putExtra("id", Integer.toString(projectId));
        projectInfoActivityRule.launchActivity(intent);  //note that userId will be null here, still matched by regex
        onView(withId(R.id.projectNameField)).perform(replaceText(newProjectName), closeSoftKeyboard());
        onView(withId(R.id.updateButton)).perform(click());
        sleep(3000);
        Intents.intended(hasComponent((ProjectListActivity.class.getName())));
        sleep(500);
        onData(hasToString(startsWith(newProjectName)))
                .inAdapterView(withId(R.id.projectList))
                .check(matches(isDisplayed()));
        onData(hasToString(startsWith(newProjectName)))
                .inAdapterView(withId(R.id.projectList))
                .perform(click());
        sleep(500);
        Intents.intended(hasComponent((ProjectInfoActivity.class.getName())),times(2));
        sleep(500);
        onView(withId(R.id.projectNameField)).check(matches(withText(newProjectName)));

        projectInfoActivityRule.finishActivity();
    }


}



