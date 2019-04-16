//package com.example.mobile3.FunctionalTest;
//
//import android.content.Intent;
//import android.support.test.espresso.Espresso;
//import android.support.test.espresso.action.ViewActions;
//import android.support.test.espresso.intent.Intents;
//import android.support.test.espresso.matcher.ViewMatchers;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.example.mobile3.LoginActivity;
//import com.example.mobile3.ProjectInfoActivity;
//import com.example.mobile3.ProjectListActivity;
//import com.example.mobile3.R;
//import com.example.mobile3.Singleton.ProjectMap;
//import com.example.mobile3.Singleton.UserMap;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.Intents.times;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.CoreMatchers.anything;
//import static org.hamcrest.core.IsNot.not;
//
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(AndroidJUnit4.class)
//public class Case7_UserDeleteProjectTest {
//    String testEmail;
//    String userIdWithProject;
//    String projectWithTime;
//    String projectWOTime;
//
//
//
//    @Before
//    public void init() {
//        //ProjectMap.getInstance().getMap().clear();
//        //UserMap.getInstance().getMap().clear();
//        //add test user
//        testEmail = "test@test.com";
//        projectWithTime = "Worked Project";
//        projectWOTime = "Unworked Project";
//
//        UserMap.getInstance().create("John", "Doe", testEmail);
//
//        userIdWithProject = UserMap.getInstance().getMap().get(testEmail).getId();
//        //ProjectMap.getInstance().create(projectWithTime, userIdWithProject);
//        ProjectMap.getInstance().getMap().get(projectWithTime).setTimeWorked(100);
//
//        //ProjectMap.getInstance().create(projectWOTime, userIdWithProject);
//        //Singleton.getInstance().setCurrentUser(testEmail);
//
//        Intents.init();
//
//
//        //login
//        loginActivityRule.launchActivity(null);
//        Espresso.onView(ViewMatchers.withId(R.id.email))
//                .perform(ViewActions.typeText(testEmail));
//        Espresso.onView(withId(R.id.userLogin))
//                .perform((ViewActions.click()));
//
//
//    }
//
//    @After
//    public void clean() {
//        UserMap.getInstance().getMap().clear();
//        ProjectMap.getInstance().getMap().clear();
//        Intents.release();
//    }
//
//
//    /**
//     * 7 User delete project tests
//     */
//
//    @Rule
//    public final ActivityTestRule<LoginActivity> loginActivityRule =
//            new ActivityTestRule<>(LoginActivity.class, true, false);
//    public final ActivityTestRule<ProjectInfoActivity> projectInfoActivity =
//            new ActivityTestRule<>(ProjectInfoActivity.class, true, false);
//    public final ActivityTestRule<ProjectListActivity> projectListActivity =
//            new ActivityTestRule<>(ProjectListActivity.class, true, true);
//
//    //7.1 User delete non-exist project test
//    @Test
//    public void userDeleteNonExistProjectTest() {
//        //currently must fail due to lack of implementation
//        Log.e("@Test","7.1 Performing user delete non-exist project test");
//        Intent intent = new Intent();
//        intent.putExtra("projectName","NonExistentProject");
//        intent.putExtra("id","888");
//        intent.putExtra("userId","888");
//        intent.putExtra("timeWorked","0");
//
//
//        projectInfoActivity.launchActivity(intent);
//        Espresso.onView(withId(R.id.deleteButton))
//                .perform(ViewActions.click());
//        Espresso.onView(withText("Project does noe exist!"))
//                .check(matches(isDisplayed()));
//
//    }
//
//    //7.2 User delete existing project
//    //7.2.1 User delete project without time worked
//    @Test
//    public void userDeleteProjectWithoutTimeWorkedTest() {
//        Log.e("@Test","7.2.1 Performing user delete project without time worked test");
//        Intent intent = new Intent();
//        intent.putExtra("projectName",projectWOTime);
//        intent.putExtra("timeWorked",ProjectMap.getInstance().getMap().get(projectWOTime).getTimeWorked());
//
//        projectInfoActivity.launchActivity(intent);
//        Espresso.onView(withId(R.id.deleteButton))
//                .perform(ViewActions.click());
//        intended(hasComponent((ProjectListActivity.class.getName())), times(2)); //including init
//        Espresso.onData(anything())
//                .inAdapterView(withId(R.id.projectList))
//                .atPosition(0)
//                .onChildView(withId(R.id.projectName))
//                .check(matches(not(withText(projectWOTime))));
//    }
//
////    //7.2.2 User delete project with time worked
////    //7.2.2.1 User delete project with time worked and cancels
////    @Test
////    public void userDeleteProjectWithTimeWokedCancel() {
////        Log.e("@Test","7.2.2.1 Performing admin delete user with project and cancel test");
////        ProjectMap.getInstance().getMap().remove(projectWOTime);//remove the one without project
////
////        Intent intent = new Intent();
////        intent.putExtra("projectName",projectWithTime);
////        intent.putExtra("timeWorked",ProjectMap.getInstance().getMap().get(projectWithTime).getTimeWorked());
////
////        projectInfoActivity.launchActivity(intent);
////        Espresso.onView(withId(R.id.deleteButton))
////                .perform(ViewActions.click());
////        Espresso.onView(withText("The current project has time worked associated with it. Do you still want to delete it?"))
////                .check(matches(isDisplayed()));
////        Espresso.onView(withId(android.R.id.button2))
////                .perform(ViewActions.click());
////        //Stays in current activity
////        intended(hasComponent(ProjectInfoActivity.class.getName()));
////        //dialog gone test?
////        //back to list and test user still there.
////        projectListActivity.launchActivity(null);
////        //intended(hasComponent(projectListActivity.class.getName()));
////        Espresso.onData(anything())
////                .inAdapterView(withId(R.id.projectList))
////                .atPosition(0)      //Assume only one left, the one did not delete.
////                .onChildView(withId(R.id.projectName))
////                .check(matches(withText(projectWithTime)));
////
////    }
////
////    //3.2.2.2 User delete project with time worked and confirms
////    @Test
////    public void adminDeleteUserWithProjectConfirm() {
////        Log.e("@Test","7.2.2.2 Performing user delete project with timeworked and confirm test");
////        Intent intent = new Intent();
////        intent.putExtra("projectName",projectWithTime);
////        intent.putExtra("timeWorked",ProjectMap.getInstance().getMap().get(projectWithTime).getTimeWorked());
////
////
////        projectInfoActivity.launchActivity(intent);
////        Espresso.onView(withId(R.id.deleteButton))
////                .perform(ViewActions.click());
////        Espresso.onView(withText("The current project has time worked associated with it. Do you still want to delete it?"))
////                .check(matches(isDisplayed()));
////        Espresso.onView(withId(android.R.id.button1))
////                .perform(ViewActions.click());
////
////        intended(hasComponent((ProjectListActivity.class.getName())), times(2)); //including init
////        //check user deleted in list view
////        Espresso.onData(anything())
////                .inAdapterView(withId(R.id.projectList))
////                .atPosition(0)      //assume only the one with project left in list (consider better ways..) and it's not what we deleted
////                .onChildView(withId(R.id.projectName))
////                .check(matches(not(withText(projectWithTime))));
////    }
//
//
//
//
//
//}
//
//
//
