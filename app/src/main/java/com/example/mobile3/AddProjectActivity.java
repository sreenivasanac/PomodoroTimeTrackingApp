package com.example.mobile3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Singleton.Singleton;
import com.example.mobile3.Singleton.ProjectMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.mobile3.ProjectListActivity;

public class AddProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText projectNameField =  (EditText) findViewById(R.id.projectNameField);
        final Button createButton = (Button) findViewById(R.id.createButton);
        final Singleton info = Singleton.getInstance(this);


        //click on create button creates new project
        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkProjectNameExistence(projectNameField.getText().toString())) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Project name existed!", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                String url = BuildConfig.API_URL+"/users/" + info.getCurrentUser().getId() + "/projects";
                JSONObject requestBody = new JSONObject();
                try{
                    requestBody.put("projectname", projectNameField.getText().toString());
                    requestBody.put("userId", info.getCurrentUser().getId());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //int userId = info.getCurrentUser().getId();
                                //projectMap.create(projectNameField.getText().toString(), userId); //dummy user ID 1, need to change to dynamic
                                //Success
                                Intent projectListIntent = new Intent(AddProjectActivity.this, ProjectListActivity.class);
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
                Singleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


            }
        });
    }

    public static boolean checkProjectNameExistence(String projectName) {
        for (Project p : ProjectListActivity.projectList) {
            if (p.getProjectName().equals(projectName)) {
                return true;
            }
        }
        return false;
    }
}
