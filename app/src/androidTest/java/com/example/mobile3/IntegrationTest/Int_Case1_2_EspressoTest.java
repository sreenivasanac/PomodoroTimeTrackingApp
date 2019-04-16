package com.example.mobile3.IntegrationTest;


import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
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
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import java.util.UUID;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasToString;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class Int_Case1_2_EspressoTest extends AndroidJUnitRunner {
    String email = "case1test@mobile3.com";
    String firstName = "John";
    String lastName = "Doe";
    String newFirstName = "Jane";
    String newLastName = "Brown";



    @Rule
    public ActivityTestRule<LoginActivity> activityRule
            = new ActivityTestRule<>(LoginActivity.class);



    @Test
    public void whenAdminAddedUser_UserIsAdded() {

        //testing values
        String adminEmail =  "1@gatech.edu";

        String userEmail = email;
        String userFirstName = firstName;
        String userLastName = lastName;

        // Login as an admin
        //onView(ViewMatchers.withId(R.id.email))
                //.perform(typeText(adminEmail), closeSoftKeyboard());
        onView(withId(R.id.adminLogin)).perform(click());

        //click add user
        onView(withId(R.id.fab)).perform(click());

        //enter new user info
        onView(withId(R.id.firstNameField))
                .perform(typeText(userFirstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField))
                .perform(typeText(userLastName), closeSoftKeyboard());
        onView(withId(R.id.emailField))
                .perform(typeText(userEmail), closeSoftKeyboard());

        //click add
        onView(withId(R.id.createButton)).perform(click());

        sleep(500);

        final int[] numberOfAdapterItems = new int[1];
        onView(withId(R.id.userList)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                //here we assume the adapter has been fully loaded already
                numberOfAdapterItems[0] = listView.getAdapter().getCount();

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        sleep(500);

        //open the user was just added by admin
        //onData(anything()).inAdapterView(withId(R.id.userList)).atPosition(numberOfAdapterItems[0] - 1).perform(click());
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        //check if the user is added with the correct name fields
        onView(withId(R.id.firstNameField))
                .check(matches(withText(userFirstName)));
        onView(withId(R.id.lastNameField))
                .check(matches(withText(userLastName)));

        //Delete user
        onView(withId(R.id.deleteButton)).perform(click());

    }

    @Test
    public void whenAdminEditedUser_UserIsUpdated() {

        //testing values
        String adminEmail = "1@gatech.edu";

        String userEmail = email;
        String userFirstName = firstName;
        String userLastName = lastName;

        String newUserFirstName = newFirstName;
        String newUserLastName = newLastName;
//
//        String userEmail = UUID.randomUUID().toString() +"new@gatech.edu";
//        String userFirstName = UUID.randomUUID().toString() +"first1";
//        String userLastName = UUID.randomUUID().toString() +"last1";

//        String newUserFirstName = UUID.randomUUID().toString() +"first2";
//        String newUserLastName = UUID.randomUUID().toString() +"last2";

        // Login as an admin
        //onView(withId(R.id.email))
                //.perform(typeText(adminEmail), closeSoftKeyboard());
        onView(withId(R.id.adminLogin)).perform(click());

        //click add user
        onView(withId(R.id.fab)).perform(click());
        sleep(1000);
        //enter new user info
        onView(withId(R.id.firstNameField))
                .perform(typeText(userFirstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField))
                .perform(typeText(userLastName), closeSoftKeyboard());
        onView(withId(R.id.emailField))
                .perform(typeText(userEmail), closeSoftKeyboard());

        //click add
        onView(withId(R.id.createButton)).perform(click());

        //open the user was just added by admin
        sleep(500);

        final int[] numberOfAdapterItems = new int[1];
        onView(withId(R.id.userList)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                //here we assume the adapter has been fully loaded already
                numberOfAdapterItems[0] = listView.getAdapter().getCount();

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        sleep(500);
        //onData(anything()).inAdapterView(withId(R.id.userList)).atPosition(numberOfAdapterItems[0] - 1).perform(click());
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());
        sleep(1000);

        //check if the user is added with the correct name fields
        onView(withId(R.id.firstNameField))
                .check(matches(withText(userFirstName)));
        onView(withId(R.id.lastNameField))
                .check(matches(withText(userLastName)));

        //edit new user info

        onView(withId(R.id.firstNameField))
                .perform(replaceText(newUserFirstName), closeSoftKeyboard());
        onView(withId(R.id.lastNameField))
                .perform(replaceText(newUserLastName), closeSoftKeyboard());

        //click update
        onView(withId(R.id.updateButton)).perform(click());

        //open the user again
        //onData(anything()).inAdapterView(withId(R.id.userList)).atPosition(numberOfAdapterItems[0] - 1).perform(click());
        onData(hasToString(startsWith(email)))
                .inAdapterView(withId(R.id.userList))
                .perform(click());

        //check if the user's info is edited with the correct name fields
        onView(withId(R.id.firstNameField))
                .check(matches(withText(newUserFirstName)));
        onView(withId(R.id.lastNameField))
                .check(matches(withText(newUserLastName)));

        //delete user
        onView(withId(R.id.deleteButton)).perform(click());

    }
}