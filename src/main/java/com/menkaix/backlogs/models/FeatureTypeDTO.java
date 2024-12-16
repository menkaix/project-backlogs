package com.menkaix.backlogs.models;

public class FeatureTypeDTO {
    public String id;
    public String name;
    public boolean isContainer = false;

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

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean isContainer) {
        this.isContainer = isContainer;
    }
}
