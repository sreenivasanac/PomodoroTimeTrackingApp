package com.example.mobile3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.mobile3.Adapter.UserAdapter;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Model.User;
import com.example.mobile3.Singleton.Singleton;
import com.example.mobile3.Singleton.UserMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    ArrayList<User> userList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed()
    {
        //always go back to login
        Intent loginIntent = new Intent(UserListActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading users ...");
        progressDialog.show();

//        final Button logoutButton = (Button) findViewById(R.id.logoutButton);
//        //click on Logout button redirects to  Login Page
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent loginIntent = new Intent(UserListActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//            }
//        });



        //request list of users
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,BuildConfig.API_URL+"/users",null,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {

                        progressDialog.dismiss();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jo  = response.getJSONObject(i);
                                User u = new User(jo.getString("id")
                                        ,jo.getString("firstName")
                                        ,jo.getString("lastName")
                                        ,jo.getString("email"));
                                userList.add(u);
                                System.out.println(u.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        UserAdapter adapter = new UserAdapter(getApplicationContext(), userList);
                        final ListView listView = (ListView) findViewById(R.id.userList);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String userId = userList.get(position).getId();
                                //String email = (String)(((User)listView.getItemAtPosition(position)).getEmail());
                                Intent userInfoIntent = new Intent(UserListActivity.this, UserInfoActivity.class);
                                userInfoIntent.putExtra("id",userId);
                                //userInfoIntent.putExtra("firstName",userMap.getMap().get(email).getFirstName());
                                //userInfoIntent.putExtra("lastName",userMap.getMap().get(email).getLastName());
                                startActivity(userInfoIntent);

                            }
                        });

                        FloatingActionButton fab = findViewById(R.id.fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent addUserIntent = new Intent(UserListActivity.this, AddUserActivity.class);
                                startActivity(addUserIntent);
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
