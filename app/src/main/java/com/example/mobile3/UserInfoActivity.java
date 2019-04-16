package com.example.mobile3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Model.User;
import com.example.mobile3.Singleton.Singleton;
import com.example.mobile3.Singleton.ProjectMap;
import com.example.mobile3.Singleton.UserMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {
    String url;
    ProgressDialog progressDialog;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving user information ...");
        progressDialog.show();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText firstNameField =  (EditText) findViewById(R.id.firstNameField);
        final EditText lastNameField =  (EditText) findViewById(R.id.lastNameField);

        final Button updateButton = (Button) findViewById(R.id.updateButton);
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);

//        final Button logoutButton = (Button) findViewById(R.id.logoutButton);
//        //click on Logout button redirects to  Login Page
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent loginIntent = new Intent(UserInfoActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//
//            }
//        });

        userId = getIntent().getExtras().getString("id");
        url = BuildConfig.API_URL+"/users/"+userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try{
                            firstNameField.setText(response.getString("firstName"));
                            lastNameField.setText(response.getString("lastName"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                JSONObject requestBody = new JSONObject();
                                try {
                                    requestBody.put("firstName", firstNameField.getText().toString());
                                    requestBody.put("lastName", lastNameField.getText().toString());
                                    requestBody.put("email", response.getString("email"));
                                }catch (JSONException e) {
                                    e.getStackTrace();
                                }

                                JsonObjectRequest UpdateUserRequest = new JsonObjectRequest
                                        (Request.Method.PUT, url, requestBody, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                //return
                                                Intent userListIntent = new Intent(UserInfoActivity.this, UserListActivity.class);
                                                startActivity(userListIntent);
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

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                JsonArrayRequest projectListRequest = new JsonArrayRequest(
                                        Request.Method.GET,BuildConfig.API_URL+"/users/" + userId + "/projects",null,
                                        new Response.Listener<JSONArray>(){
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                boolean hasProject = false;
                                                if (response.length()>0) hasProject = true;
                                                if (hasProject) deleteAlert(); else deleteUser();

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse response = error.networkResponse;
                                        if(response != null && response.data != null){
                                            if (error.networkResponse.statusCode == 404) {
                                                userNotFoundAlert();
                                            }
                                        }
                                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                                        System.out.println("ERROR: " + error);
                                        //Log.d("DELETE USER",error.getMessage());
                                        toast.show();

                                    }

                                });

                                Singleton.getInstance(getApplicationContext()).addToRequestQueue(projectListRequest);


                            }
                        });


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                        System.out.println("ERROR: " + error.getMessage());
                        toast.show();
                    }
                });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);



    }

    private void deleteUser() {
        JsonObjectRequest deleteUserRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Success, refresh? or return to user list?

                        Intent userListIntent = new Intent(UserInfoActivity.this, UserListActivity.class);
                        startActivity(userListIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            if (error.networkResponse.statusCode == 404) {
                                userNotFoundAlert();
                            }
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                        System.out.println("ERROR: " + error.networkResponse);
                        toast.show();
                    }
                });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(deleteUserRequest);
    }

    private void deleteAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                UserInfoActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Delete User");
        // Setting Dialog Message
        alertDialog.setMessage(R.string.confirm_delete_user_with_project);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
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

    private void userNotFoundAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                UserInfoActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Delete User");
        // Setting Dialog Message
        alertDialog.setMessage(R.string.error_user_not_found);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable);
        alertDialog.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }


    private void debugAlert(final String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                UserInfoActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Debug");
        // Setting Dialog Message
        alertDialog.setMessage(msg);
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
