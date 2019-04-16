package com.example.mobile3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mobile3.Adapter.ReportSessionAdapter;
import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.Singleton.ProjectMap;
import com.example.mobile3.Singleton.Singleton;
import com.example.mobile3.Singleton.UserMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import com.example.mobile3.AddProjectActivity;

public class ProjectInfoActivity extends AppCompatActivity {
    String url;
    ProgressDialog progressDialog;
    String projectId;
    String userId;
    boolean hasTimeWorked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving project information ...");
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText projectNameField =  (EditText) findViewById(R.id.projectNameField);
        //final EditText timeWorkedField =  (EditText) findViewById(R.id.timeWorkedField);
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final Button updateButton = (Button) findViewById(R.id.updateButton);
        final Button reportButton = (Button) findViewById(R.id.reportButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectId = extras.getString("id");
//            timeWorked = extras.getString("timeWorked");
//            id = extras.getInt("id");
//            userId = extras.getInt("userId");
//            projectNameField.setText(projectName);
//            projectNameField.setEnabled(false);
//            timeWorkedField.setText(timeWorked);
        }
        userId = Singleton.getInstance(this).getCurrentUser().getId();

        url = BuildConfig.API_URL+"/users/"+userId + "/projects/" + projectId;
        JsonObjectRequest projectInfoRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        //parse project info
                        try {
                            projectNameField.setText(response.getString("projectname"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //display buttons

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hasTimeWorked  = false;
                                Date date = new Date();
                                String to = String.format("%02d", date.getYear()+1900) + "-" + String.format("%02d", date.getMonth()) +"-"+String.format("%02d", date.getDate())+"T" + String.format("%02d", date.getHours()) + ":" +String.format("%02d", date.getMinutes()) + "Z";
                                // Initialize a new JsonArrayRequest instance
                                JsonObjectRequest reportRequest = new JsonObjectRequest(
                                        Request.Method.GET,url+"/report?from=2019-01-01T00:00Z&to="+to,null,
                                        new Response.Listener<JSONObject>(){
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("NETWORK_RESPONSE", response.toString());
                                                //Load data
                                                String pomodorosNum = "Not Requested";
                                                String totalHours = "Not Requested";
                                                try {
                                                    JSONArray sessions = new JSONArray(response.getString("sessions"));
                                                    if (sessions.length() > 0){
                                                        hasTimeWorked = true;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                //Render Session List
                                                if (hasTimeWorked) deleteAlert(); else deleteProject();
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        String errorString = error.getMessage();
                                        try {
                                            String responseBody = new String( error.networkResponse.data, "utf-8" );
                                            //errorString = responseBody;
                                            JSONObject jsonObject = new JSONObject( responseBody );
                                            if (jsonObject.getInt("status") == 404) {
                                            }
                                            Log.d("NETWORK", "responseBODY = "+responseBody);

                                        } catch ( JSONException e ) {
                                            //Handle a malformed json response
                                        } catch (UnsupportedEncodingException err){
                                        }
                                        Toast toast = Toast.makeText(getApplicationContext(),errorString,Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                                Singleton.getInstance(getApplicationContext()).addToRequestQueue(reportRequest);


                            }
                        });

                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (AddProjectActivity.checkProjectNameExistence(projectNameField.getText().toString())) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Project name existed!", Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }
                                JSONObject requestBody = new JSONObject();
                                try {
                                    requestBody.put("projectname", projectNameField.getText().toString());
                                }catch (JSONException e) {
                                    e.getStackTrace();
                                }
                                JsonObjectRequest UpdateUserRequest = new JsonObjectRequest
                                        (Request.Method.PUT, url, requestBody, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Intent projectListIntent = new Intent(ProjectInfoActivity.this, ProjectListActivity.class);
                                                startActivity(projectListIntent);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                                                System.out.println("ERROR: " + error.getMessage());
                                                toast.show();
                                            }
                                        });
                                Singleton.getInstance(getApplicationContext()).addToRequestQueue(UpdateUserRequest);

                            }
                        });


                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                        System.out.println("ERROR: " + error.getMessage());
                        toast.show();
                    }
                });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(projectInfoRequest);




        CheckBox report_pom = findViewById(R.id.checkbox_includePomodoros);
        CheckBox report_hour = findViewById(R.id.checkbox_includeHours);

        TextView reportDateStart;
        TextView reportTimeStart;

        DatePickerDialog.OnDateSetListener mDateSetListenerStart;
        TimePickerDialog.OnTimeSetListener mTimeSetListenerStart;

        reportDateStart = (TextView) findViewById(R.id.reportDateStart);
        reportTimeStart = (TextView) findViewById(R.id.reportTimeStart);

        reportDateStart.setText("2019-01-01");
        reportTimeStart.setText("00:00");
        mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                reportDateStart.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
            }
        };

        mTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                reportTimeStart.setText( time);
            }
        };

        reportDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(ProjectInfoActivity.this,
                        android.R.style.Theme_Material_Dialog_MinWidth,
                        mDateSetListenerStart, year, month, day);
                dialog.show();
            }
        });

        reportTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ProjectInfoActivity.this,
                        android.R.style.Theme_Material_Dialog_MinWidth,
                        mTimeSetListenerStart, hour, minute, true);
                dialog.show();
            }
        });


        TextView reportDateEnd;
        TextView reportTimeEnd;
        DatePickerDialog.OnDateSetListener mDateSetListenerEnd;
        TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd;

        reportDateEnd = (TextView) findViewById(R.id.reportDateEnd);
        reportTimeEnd = (TextView) findViewById(R.id.reportTimeEnd);
        Calendar cal = Calendar.getInstance();
        reportDateEnd.setText(cal.get(Calendar.YEAR) + "-" + String.format("%02d", cal.get(Calendar.MONTH)+1) + "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
        reportTimeEnd.setText(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", cal.get(Calendar.MINUTE)));
        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                reportDateEnd.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
            }
        };

        mTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                reportTimeEnd.setText( time);
            }
        };

        reportDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(ProjectInfoActivity.this,
                        android.R.style.Theme_Material_Dialog_MinWidth,
                        mDateSetListenerEnd, year, month, day);
                dialog.show();
            }
        });

        reportTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ProjectInfoActivity.this,
                        android.R.style.Theme_Material_Dialog_MinWidth,
                        mTimeSetListenerEnd, hour, minute, true);
                dialog.show();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Build report request body
                String fromTime = reportDateStart.getText().toString() + "T" +reportTimeStart.getText().toString() + "Z";
                String toTime = reportDateEnd.getText().toString() + "T" +reportTimeEnd.getText().toString() + "Z";

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("from",fromTime);
                    requestBody.put("to",toTime);
                    requestBody.put("includeCompletedPomodoros",report_pom.isChecked());
                    requestBody.put("includeTotalHoursWorkedOnProject",report_hour.isChecked());
                }catch (JSONException e) {
                    e.getStackTrace();
                }

                String reportUrl = url+"/report?" +"from=" + fromTime + "&to=" + toTime + "&includeCompletedPomodoros=" + report_pom.isChecked() + "&includeTotalHoursWorkedOnProject=" + report_hour.isChecked();
                Intent reportIntent = new Intent(ProjectInfoActivity.this, ReportActivity.class);
                                reportIntent.putExtra("requestBody", requestBody.toString());
                                reportIntent.putExtra("url", reportUrl);
                startActivity(reportIntent);

            }
        });

    }

    private void deleteProject() {

        JsonObjectRequest UpdateUserRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Success, refresh? or return to user list?

                        Intent projectListIntent = new Intent(ProjectInfoActivity.this, ProjectListActivity.class);
                        startActivity(projectListIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                        System.out.println("ERROR: " + error.getMessage());
                        toast.show();
                    }
                });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(UpdateUserRequest);


    }

    private void deleteAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ProjectInfoActivity.this);
        alertDialog.setTitle("Delete Project");
        alertDialog.setMessage(R.string.confirm_delete_project_with_hours);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProject();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }




}



