package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

import java.util.WeakHashMap;

public class Feature extends AbstractEntity {

    @Id
    private String id;

    private String description;

    private String type;

    private String storyId;

    private String parentID;

    private WeakHashMap<String, String> properties = new WeakHashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public WeakHashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(WeakHashMap<String, String> properties) {
        this.properties = properties;
    }
}
