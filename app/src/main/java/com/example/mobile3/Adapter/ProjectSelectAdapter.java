
package com.example.mobile3.Adapter;


//Customized Adapter for Projects

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobile3.Model.Project;
import com.example.mobile3.R;

import java.util.ArrayList;

public class ProjectSelectAdapter extends ArrayAdapter<Project> {
    public ProjectSelectAdapter(Context context, ArrayList<Project> projects) {
        super(context, 0, projects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Project project = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_project_select, parent, false);
        }
        // Lookup view for data population
        TextView projectName = (TextView) convertView.findViewById(R.id.projectName);
        //TextView timeWorked = (TextView) convertView.findViewById(R.id.timeWorked);

        // Populate the data into the template view using the data object
        projectName.setText(project.getProjectName());
        //timeWorked.setText(project.getTimeWorked().toString());


        // Return the completed view to render on screen
        return convertView;
    }
}