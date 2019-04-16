package com.example.mobile3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile3.Adapter.ProjectAdapter;
import com.example.mobile3.Model.*;
import com.example.mobile3.Singleton.ProjectMap;
import com.example.mobile3.Singleton.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectListActivity extends AppCompatActivity {
    public static ArrayList<Project> projectList = new ArrayList<>();

    @Override
    public void onBackPressed()
    {
        //Go back to PomodoroSession Page
        Intent loginIntent = new Intent(ProjectListActivity.this, PomodoroSessionActivity.class);
        startActivity(loginIntent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        final Singleton info = Singleton.getInstance(this);
        //final Button viewPomodoroSessionButton2 = (Button) findViewById(R.id.viewPomodoroSessionButton2);
//        final Button logoutButton = (Button) findViewById(R.id.logoutButton);
        final TextView NoProjectAvailableTextView = (TextView) findViewById(R.id.NoProjectAvailableTextView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        //   On clicking "ViewPomodoros" Button, Redirect to Pomodoro Page
//        viewPomodoroSessionButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent userIntent = new Intent(ProjectListActivity.this, PomodoroSessionActivity.class);
//                startActivity(userIntent);
//            }
//        });

//        //click on Logout button redirects to  Login Page
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent loginIntent = new Intent(ProjectListActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//
//            }
//        });



        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,BuildConfig.API_URL+"/users/"+info.getCurrentUser().getId()+"/projects",null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        //Load Project List
                        try {
                            projectList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jo  = response.getJSONObject(i);
                                Project p = new Project(jo.getString("id")
                                                        ,jo.getString("projectname")
                                                        ,jo.getString("userId"));
                                projectList.add(p);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Render Project List
                        ProjectAdapter adapter = new ProjectAdapter(getApplicationContext(), projectList);
                        final ListView listView = (ListView) findViewById(R.id.projectList);
                        listView.setAdapter(adapter);


                        if (projectList.size() == 0){
                            NoProjectAvailableTextView.setText("No Projects Created Yet!");
                        } else {
                            NoProjectAvailableTextView.setText("");
                        }

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String projectId = projectList.get(position).getId();
                                //long projectId = projectList.get(position).getId();
                                //String projectName = listView.getItemAtPosition(position).toString();
                                Intent projectIntent = new Intent(ProjectListActivity.this, ProjectInfoActivity.class);
                                projectIntent.putExtra("id",projectId);
                                startActivity(projectIntent);
                            }
                        });

                        FloatingActionButton fab = findViewById(R.id.fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent projectListIntent = new Intent(ProjectListActivity.this, AddProjectActivity.class);
                                startActivity(projectListIntent);
                            }
                        });


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CharSequence seq = "Unable to load Users.";
                Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                System.out.println("ERROR: " + error.getMessage());
                toast.show();
            }
        });
        Singleton.getInstance(this).addToRequestQueue(jsonArrayRequest);


    }

}
