package com.example.mobile3.Singleton;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mobile3.Model.*;

public class Singleton {

    private static Singleton instance;
    private RequestQueue requestQueue;
    public static Singleton getInstance(Context c){
        if(instance == null) instance = new Singleton(c);
        return instance;
    }

    public Singleton(Context c) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(c);
            currentUser = new User();
        }
    }

    private User currentUser;


    public User getCurrentUser() {return currentUser;}

//    public boolean setCurrentUser(String email) {
//        for (User u : UserMap.getInstance().getMap().values()) {
//            if (u.getEmail().equals(email)) {
//                currentUser = u;
//                return true;
//            }
//        }
//        return false; //user not found
//    }

    public void setCurrentUser(User u) {
        currentUser = u;
    }

    public void addToRequestQueue(Request request) {
        requestQueue.add(request);
    }
}
