package com.example.mobile3.Singleton;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile3.LoginActivity;
import com.example.mobile3.Model.Project;
import com.example.mobile3.Model.User;

import java.util.HashMap;
import java.util.Map;

public class UserMap {
    private int idCounter = 1;
    private static UserMap instance;
    private Map<String, User> map = new HashMap<>();
    public static UserMap getInstance(){
        //if(instance == null) instance = new UserMap();
        return instance;
    }

    public static void initialize(Context c) {
        instance = new UserMap(c);
    }

    /**
     * Load data using GET from server about list of all users when initializing
     */
    public UserMap(Context c){
//        map = new HashMap<>();
//        User user1 = new User(1, "f1", "l1", "1@gatech.edu");
//        User user2 = new User(2, "f2", "l2", "2@gatech.edu");
//        User user3 = new User(3, "f3", "l3", "3@gatech.edu");
//        User user4 = new User(4, "f4", "l4", "4@gatech.edu");
//        map.put(user1.getEmail(), user1);
//        map.put(user2.getEmail(), user2);
//        map.put(user3.getEmail(), user3);
//        map.put(user4.getEmail(), user4);
//        idCounter = 5;



    }
    public Map<String, User> getMap(){
        return map;
    }

    //create user
    public void create(String firstName, String lastName, String email) {
        //map.put(email, new User(idCounter, firstName, lastName, email));
        idCounter ++;
    }
}
