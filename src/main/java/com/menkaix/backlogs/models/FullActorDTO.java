package com.menkaix.backlogs.models;

import com.menkaix.backlogs.utilities.values.ActorType;
import java.util.ArrayList;
import java.util.List;

public class FullActorDTO {
    private String id;
    private String name;
    private String projectName;
    private String description;
    private ActorType type;
    private List<FullStoryDTO> stories = new ArrayList<>();

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActorType getType() {
        return type;
    }

    public void setType(ActorType type) {
        this.type = type;
    }

    public List<FullStoryDTO> getStories() {
        return stories;
    }

    public void setStories(List<FullStoryDTO> stories) {
        this.stories = stories;
    }
}
