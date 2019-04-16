package com.example.mobile3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Patterns;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mobile3.Singleton.*;

import org.json.JSONException;
import org.json.JSONObject;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText firstName =  (EditText) findViewById(R.id.firstNameField);
        final EditText lastName =  (EditText) findViewById(R.id.lastNameField);
        final EditText email =  (EditText) findViewById(R.id.emailField);

        final Button createButton = (Button) findViewById(R.id.createButton);
        final UserMap userMap = UserMap.getInstance();

        //click on create button creates new project
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedEmail = email.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Email is invalid!", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                String url = BuildConfig.API_URL+"/users/";
                JSONObject requestBody = new JSONObject();
                try{
                    requestBody.put("firstName", firstName.getText().toString());
                    requestBody.put("lastName", lastName.getText().toString());
                    requestBody.put("email", email.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Intent userListIntent = new Intent(AddUserActivity.this, UserListActivity.class);
                                startActivity(userListIntent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.getMessage() != null){
                                    Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                                    System.out.println("ERROR: " + error.getMessage());
                                    toast.show();
                                } else if (error.networkResponse.statusCode == 409) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_LONG);
                                    System.out.println(error.networkResponse.statusCode);
                                    toast.show();
                                }
                            }


                        });
                Singleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

            }
        });


    }

}
