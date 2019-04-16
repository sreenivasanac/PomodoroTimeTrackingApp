package com.example.mobile3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mobile3.Adapter.ProjectAdapter;
import com.example.mobile3.Adapter.ReportSessionAdapter;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.Singleton.Singleton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    ArrayList<ReportSession> reportSessions = new ArrayList<ReportSession>();
    TextView completedPomodoros = null;
    TextView totalHoursWorked = null;
    String url;

//    @Override
//    public void onBackPressed()
//    {
//        //always go back to login
//        Intent loginIntent = new Intent(ReportActivity.this, LoginActivity.class);
//        startActivity(loginIntent);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        totalHoursWorked = findViewById(R.id.totalHoursWorked);
        completedPomodoros = findViewById(R.id.completedPomodoros);
        LinearLayout reportLL =  findViewById(R.id.ll_report);
        LinearLayout completPomLL = findViewById(R.id.ll_completePomodoros);
        LinearLayout hoursWorkedLL = findViewById(R.id.ll_hoursWorked);

        JSONObject requestBody = null;
        try{
            requestBody = new JSONObject(getIntent().getStringExtra("requestBody"));
            //hiding
            if (!requestBody.getBoolean("includeCompletedPomodoros")) {
               reportLL.removeView(completPomLL);
               completedPomodoros = null;
            }
            if (!requestBody.getBoolean("includeTotalHoursWorkedOnProject")) {
                reportLL.removeView(hoursWorkedLL);
                totalHoursWorked = null;

            }

            url = getIntent().getStringExtra("url");
            Log.d("NETWORK_REQUEST", url);

        }catch (JSONException e) {
            e.printStackTrace();
        }



        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest reportRequest = new JsonObjectRequest(
                Request.Method.GET,url,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("NETWORK_RESPONSE", response.toString());
                        //Load data
                        String pomodorosNum = "Not Requested";
                        String totalHours = "Not Requested";
                        try {
                            JSONArray sessions = new JSONArray(response.getString("sessions"));
                            for (int i = 0; i < sessions.length(); i++) {
                                JSONObject jo  = sessions.getJSONObject(i);
                                ReportSession rp = new ReportSession(jo.getString("startingTime")
                                                        ,jo.getString("endingTime")
                                                        ,jo.getDouble("hoursWorked"));
                                reportSessions.add(rp);
                            }
                            if(response.has("completedPomodoros"))
                                pomodorosNum = response.getString("completedPomodoros");
                            if(response.has("totalHoursWorkedOnProject"))
                                totalHours = response.getString("totalHoursWorkedOnProject");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Render project info
                        double hours = 0;
                        int hour, minutes;
                        if (completedPomodoros != null)
                            completedPomodoros.setText(pomodorosNum);
                        if (totalHoursWorked != null) {
                            if (totalHours != "Not Requested") {
                                hours = Double.valueOf(totalHours);
                                hour = (int)hours/1;
                                minutes = (int)(hours*60) % 60;
                                totalHours = hour+"h"+minutes+"m";
                            }
                            totalHoursWorked.setText(totalHours);
                        }


                        //Render Session List
                        ReportSessionAdapter adapter = new ReportSessionAdapter(getApplicationContext(), reportSessions);
                        final ListView listView = (ListView) findViewById(R.id.reportSessionList);
                        listView.setAdapter(adapter);


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
                        errorString = getResources().getString(R.string.error_report_not_found);
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
        Singleton.getInstance(this).addToRequestQueue(reportRequest);


    }

}
