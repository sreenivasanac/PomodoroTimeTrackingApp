package com.example.mobile3.Adapter;

//https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
//Customized Adapter for Users

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobile3.Model.ReportSession;
import com.example.mobile3.Model.User;
import com.example.mobile3.R;

import java.util.ArrayList;

public class ReportSessionAdapter extends ArrayAdapter<ReportSession> {
    public ReportSessionAdapter(Context context, ArrayList<ReportSession> reportSessions) {
        super(context, 0, reportSessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ReportSession reportSession = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_report_session, parent, false);
        }
        // Lookup view for data population
        TextView item = (TextView) convertView.findViewById(R.id.reportSession);
        TextView hoursWorked = (TextView) convertView.findViewById(R.id.hoursWorked);

        // Populate the data into the template view using the data object
        item.setText(reportSession.getStartingTime() + "\n" + reportSession.getEndingTime());

        double hours = reportSession.getHoursWorked();
        int hour = (int)hours/1;
        int minutes = (int)(hours*60) % 60;
        String totalHours = hour+"h"+minutes+"m";


        hoursWorked.setText( "\n" +totalHours);


        // Return the completed view to render on screen
        return convertView;
    }
}