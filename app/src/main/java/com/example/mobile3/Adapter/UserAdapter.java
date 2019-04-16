package com.example.mobile3.Adapter;

//https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
//Customized Adapter for Users

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobile3.Model.*;
import com.example.mobile3.R;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, ArrayList<User> projects) {
        super(context, 0, projects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView email = (TextView) convertView.findViewById(R.id.email);

        // Populate the data into the template view using the data object
        email.setText(user.getEmail());


        // Return the completed view to render on screen
        return convertView;
    }
}