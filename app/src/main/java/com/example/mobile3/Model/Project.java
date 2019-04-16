package com.example.mobile3.Model;


public class Project {
    private String id;
    private String projectName;
    private String userId;
    private Integer timeWorked;

    public Project(String id, String projectName, String userId){
        this.id = id;
        this.projectName = projectName;
        this.userId = userId;
        this.timeWorked = 0;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() { return projectName;}

    public void setTimeWorked(int timeWorked) {
        this.timeWorked = timeWorked;
    }

    public Integer getTimeWorked() {
        return timeWorked;
    }
}
