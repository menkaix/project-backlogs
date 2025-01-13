package com.menkaix.backlogs.entities;

import com.menkaix.backlogs.utilities.values.ActorType;
import org.springframework.data.annotation.Id;

public class Actor extends AbstractEntity {

    @Id
    private String id;

    private String projectName;

    private String description;

    private ActorType type = ActorType.USER;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
