package com.menkaix.backlogs.models.dto;

import java.util.ArrayList;
import java.util.List;

public class FeatureTreeDTO {
    private String id;
    private String name;
    private String description;
    private String parentID;
    private String type;
    private List<FeatureTreeDTO> children = new ArrayList<>();

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

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FeatureTreeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<FeatureTreeDTO> children) {
        this.children = children;
    }
}
