package com.example.mobile3.Singleton;

import com.example.mobile3.Model.Project;
import com.example.mobile3.Model.User;

import java.util.HashMap;
import java.util.Map;

public class ProjectMap {
    private int idCounter = 1;
    private static ProjectMap instance;
    private Map<String, Project> map;
    public static ProjectMap getInstance(){
        if(instance == null) instance = new ProjectMap();
        return instance;
    }
    public ProjectMap(){
        map = new HashMap<>();
//        Project p1 = new Project(1, "project1", 1);
//        Project p2 = new Project(2, "project2", 2);
//        Project p3 = new Project(3, "project3", 3);
////        Project p4 = new Project(4, "project4", 4);
//        map.put(p1.getProjectName(), p1);
//        map.put(p2.getProjectName(), p2);
//        map.put(p3.getProjectName(), p3);
//        map.put(p4.getProjectName(), p4);
        idCounter = 5;

    }
    public Map<String, Project> getMap(){
        return map;
    }

    //create project
    public void create(String projectName, int userId) {
        //map.put(projectName, new Project(idCounter, projectName, userId));
        idCounter ++;
    }

}
