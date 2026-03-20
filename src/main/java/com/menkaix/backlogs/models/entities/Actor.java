package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.menkaix.backlogs.models.values.ActorType;

@Document(collection = "actor")
public class Actor extends AbstractEntity {

    @Id
    private String id;

    @Indexed
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
