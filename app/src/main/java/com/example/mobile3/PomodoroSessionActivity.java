package com.example.mobile3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.TextView;

import android.os.CountDownTimer;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mobile3.Adapter.ProjectAdapter;
import com.example.mobile3.Adapter.ProjectSelectAdapter;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Singleton.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;


public class PomodoroSessionActivity extends AppCompatActivity {

    private Date starting;
    private Date ending;
    private Integer counter = 0;

    public int workLength = BuildConfig.WORK_TIME;  //in seconds
    public int breakLength = BuildConfig.BREAK_TIME;

    boolean paused = false;
    Button startButton;

    @Override
    public void onBackPressed() {
        //Go back to login Page
        Intent loginIntent = new Intent(PomodoroSessionActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    CountDownTimer PomodorocountDownTimer;
    Boolean isCountDownStarted = Boolean.FALSE;
    boolean isProjectAssociated = false;
    boolean standAloneMode = false;
    String selectedProjectId = null;

    //for assoc project
    TextView counterDisplay;
    TextView assocProjectDisplay;
    int selectedProjectIndex;
    TextView countDownTimerView;

    boolean onBreak = false;

    private void startTimer() {
        Integer secondsRemaining; // 25 minutes
        if (isCountDownStarted == Boolean.TRUE) {
            secondsRemaining = convertTimeStringToSeconds(countDownTimerView.getText().toString());
        } else {
            secondsRemaining = workLength; // set working time
        }
        if (PomodorocountDownTimer != null) {
            PomodorocountDownTimer.cancel();
        }

        starting = new Date();
//                PomodorocountDownTimer.cancel();
        createCountDownTimer(secondsRemaining);
        PomodorocountDownTimer.start();
        isCountDownStarted = Boolean.TRUE;
        startButton.setText("Pause");
        countDownTimerView.setTextColor(Color.parseColor("#fa8100"));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startButton = (Button) findViewById(R.id.startButton);
        //final Button stopButton = (Button) findViewById(R.id.stopButton);
        final Button resetButton = (Button) findViewById(R.id.resetButton);
        final Button viewProjectsButton = (Button) findViewById(R.id.viewProjectsButton);
//        final Button logoutButton = (Button) findViewById(R.id.logoutButton);
        //final Button confirmProjectButton = (Button) findViewById(R.id.confirmProjectButton);
        assocProjectDisplay = findViewById(R.id.assocProjectDisplay);
        counterDisplay = findViewById(R.id.counterDisplay);
        final Singleton info = Singleton.getInstance(this);
        countDownTimerView = (TextView) findViewById(R.id.countDownTimer);

        String userId = Singleton.getInstance(this).getCurrentUser().getId();
        countDownTimerView.setText(getTimeString(workLength));




//        //click on Logout button redirects to  Login Page
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent loginIntent = new Intent(PomodoroSessionActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//
//            }
//        });

        //click on Start button prompt for assoc project if timer hasn't started
        //once started, start button becomes pause button
        //start button reset on reset.
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isProjectAssociated && !standAloneMode) {
                    showAlert();
                    counter = 0;
                }

                if (onBreak) {
                    //If on break, button cancel break and start new pomodoro
                    onBreak = false;
                    ending = new Date();
                    // Alert to ask if new Pomodoro
                    PomodorocountDownTimer.cancel();
                    countDownTimerView.setText(getTimeString(workLength));
                    countDownTimerView.setTextColor(Color.GRAY);
                    startButton.setText("Pause");
                    alertAnotherPPT();
                } else if (!paused) {
                    final TextView countDownTimerView = (TextView) findViewById(R.id.countDownTimer);
                    if (PomodorocountDownTimer != null) {
                        PomodorocountDownTimer.cancel();
                        paused = true;
                        startButton.setText("Resume");
                    }
                } else if (paused) { //paused, restart
                    startTimer();
                    paused = false;
                }
            }
        });


        //click on Reset button resets the CountDown Timer
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PomodorocountDownTimer != null) {
                    PomodorocountDownTimer.cancel();
                }
                if (isCountDownStarted && isProjectAssociated) {
                    alertPartialPPT(selectedProjectId);
                } else if (isCountDownStarted || counter > 0) {
                    ending = new Date();
                    showSummary();
                }
                reset();

            }
        });



        //click on View projects button should redirect to View Projects page
        viewProjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent userIntent = new Intent(PomodoroSessionActivity.this, ProjectListActivity.class);
                startActivity(userIntent);
            }
        });

//        //When Confirm button is clicked, after Project is selected
//        confirmProjectButton.setOnClickListener(new View.OnClickListener() {
//
//        });
    }

    private String getTimeString(int seconds) {
        return (String.format("%02d",seconds/60) + ":" + String.format("%02d",seconds%60));
    }

    private void createCountDownTimer(Integer seconds) {
//        https://stackoverflow.com/a/17383939/3766839
        final TextView countDownTimerView = (TextView) findViewById(R.id.countDownTimer);
        PomodorocountDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            String numberPadding = "";

            public void onTick(long millisUntilFinished) {
                if (((millisUntilFinished / 1000) % 60) < 10) {
                    numberPadding = "0";
                } else {
                    numberPadding = "";
                }
                countDownTimerView.setText("" + (millisUntilFinished / (60 * 1000)) + ":" + numberPadding + ((millisUntilFinished / 1000) % 60));
            }

            public void onFinish() {
                if (!onBreak) {
                    // Increase counter here
                    counter = counter + 1;
                    counterDisplay.setText(counter.toString());
                    startButton.setText("Cancel Break");
                    onBreak = true;
                    createCountDownTimer(breakLength);
                    countDownTimerView.setTextColor(Color.parseColor("#2196F3"));

                    PomodorocountDownTimer.start();
                }else if (onBreak) {
                    onBreak = false;
                    ending = new Date();
                    // Alert to ask if new Pomodoro
                    PomodorocountDownTimer.cancel();
                    countDownTimerView.setText(getTimeString(workLength));
                    countDownTimerView.setTextColor(Color.GRAY);
                    alertAnotherPPT();
                }

            }

        };
    }


    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PomodoroSessionActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Associated Projects Confirmation");
        // Setting Dialog Message
        alertDialog.setMessage(R.string.confirm_associated_pomodoro_with_projects);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                      Create association of project with session
                        showProjectsListOptions();
                        dialog.cancel();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        isProjectAssociated = false;
                        standAloneMode = true;
                        assocProjectDisplay.setText("Temporary Session");
                        startTimer();
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }

    private void alertAnotherPPT() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PomodoroSessionActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Another Pomodoro?");
        // Setting Dialog Message
        //alertDialog.setMessage("Another Pomodoro?");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                      Create association of project with session

//                      Show Project List options for user to select the Project
                        startButton.setText("Pause");
                        countDownTimerView.setTextColor(Color.parseColor("#fa8100"));
                        createCountDownTimer(workLength);
                        PomodorocountDownTimer.start();
                        dialog.cancel();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        ending = new Date();
                        if (isProjectAssociated) {
                            postSession(selectedProjectId);
                        }
                        showSummary();
                        reset();
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }

    private void alertPartialPPT(String projectId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PomodoroSessionActivity.this);
        alertDialog.setTitle("Partial Pomodoro Confirmation");
        alertDialog.setMessage("Record Partial Pomodoro?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ending = new Date();
                        showSummary();
                        postSession(projectId);
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (counter == 0) {
                            ending = starting;
                        }
                        showSummary();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    private void showSummary() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PomodoroSessionActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Pomodoro Summary");
        // Setting Dialog Message
        alertDialog.setMessage("You have finished " + counter + " Pomodoros" + " that started at " + starting + " and ended at " + ending + ".");
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    ProgressDialog progressDialog;
    private void showProjectsListOptions() {
        final Singleton info = Singleton.getInstance(this);
        ArrayList<Project> projectList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading projects ...");
        progressDialog.show();

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, BuildConfig.API_URL + "/users/" + info.getCurrentUser().getId() + "/projects", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressDialog.dismiss();
                        //Load Project List
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jo = response.getJSONObject(i);
                                Project p = new Project(jo.getString("id")
                                        , jo.getString("projectname")
                                        , jo.getString("userId"));
                                projectList.add(p);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Render Project List
                        ProjectSelectAdapter adapter = new ProjectSelectAdapter(getApplicationContext(), projectList);

                        // setup the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(PomodoroSessionActivity.this);
                        builder.setTitle("Choose a Project");

                        // add a radio button list
                        int checkedItem = 0;
                        builder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // user checked an item
                                selectedProjectIndex = which;

                            }
                        });

                        if (projectList.size() > 0) {
                            // add OK and Cancel buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // user clicked OK
                                    assocProjectDisplay.setText(projectList.get(selectedProjectIndex).getProjectName());
                                    selectedProjectId = projectList.get(selectedProjectIndex).getId();
                                    Log.d("ASSOC_PROJ", selectedProjectId);
                                    isProjectAssociated = true;
                                    startTimer();
                                }
                            });
                        } else {
                            builder.setMessage("You do not have any project!");
                        }

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                standAloneMode = true;
                                assocProjectDisplay.setText("Temporary Session");
                                startTimer();
                            }
                        });

                        // create and show the alert dialog
                        //AlertDialog dialog = builder.create();
                        builder.show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                CharSequence seq = "Unable to load Users.";
                Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                System.out.println("ERROR: " + error.getMessage());
                toast.show();
            }
        });

        Singleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


    private Integer convertTimeStringToSeconds(String TimeString) {
        String[] timeInCountDown = TimeString.split(":");
        Integer minutes = Integer.parseInt(timeInCountDown[0]);
        Integer seconds = Integer.parseInt(timeInCountDown[1]);
        Integer totalSeconds = minutes * 60 + seconds;
        return totalSeconds;
    }

    private String formatTime(Date date) {
        return String.format("%02d", date.getYear()+1900) + "-" + String.format("%02d", date.getMonth()+1) +"-"+String.format("%02d", date.getDate())+"T" + String.format("%02d", date.getHours()) + ":" +String.format("%02d", date.getMinutes()) + "Z";
    }

    private void postSession(String projectId) {
        // When a particular Project is clicked and Confirm button is clicked
        final Singleton info = Singleton.getInstance(this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("startTime", formatTime(starting));
            requestBody.put("endTime", formatTime(ending));
            requestBody.put("counter", counter);
        } catch (JSONException e) {
            e.getStackTrace();
        }
        Log.d("REQUEST", requestBody.toString());
        String url = BuildConfig.API_URL + "/users/" + info.getCurrentUser().getId() + "/projects/" + projectId + "/sessions";
        JsonObjectRequest postSessionRequest = new JsonObjectRequest
                (Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Successfully Recorded", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                        System.out.println("ERROR: " + error.getMessage());
                        toast.show();
                    }
                });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(postSessionRequest);

    }

    private void reset() {
        isCountDownStarted = false;
        isProjectAssociated = false;
        selectedProjectId = null;
        counter = 0;
        standAloneMode = false;
        counterDisplay.setText("0");
        assocProjectDisplay.setText(R.string.greeting_pomodoro_session);
        countDownTimerView.setTextColor(Color.GRAY);
        onBreak = false;
        paused = false;
        countDownTimerView.setText(getTimeString(workLength));

        startButton.setText("Start");
    }
}
