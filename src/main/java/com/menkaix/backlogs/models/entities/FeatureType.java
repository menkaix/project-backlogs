package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class FeatureType {

    @Id
    public String id;

    public String name;

    public ArrayList<String> keyFeatures = new ArrayList<>();
    public WeakHashMap<String, String> usualTask = new WeakHashMap<>();

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

    public ArrayList<String> getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(ArrayList<String> keyFeatures) {
        this.keyFeatures = keyFeatures;
    }

    public WeakHashMap<String, String> getUsualTask() {
        return usualTask;
    }

    public void setUsualTask(WeakHashMap<String, String> usualTask) {
        this.usualTask = usualTask;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean isContainer) {
        this.isContainer = isContainer;
    }
}
