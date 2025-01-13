package com.menkaix.backlogs.models;

import java.util.ArrayList;

public class FullFeatureDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private String parentID;
    private ArrayList<FullTaskDTO> tasks = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public ArrayList<FullTaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<FullTaskDTO> tasks) {
        this.tasks = tasks;
    }
}
