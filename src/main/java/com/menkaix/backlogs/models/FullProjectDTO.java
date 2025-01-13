package com.menkaix.backlogs.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullProjectDTO {
    private String id;
    private String name;
    private String code;
    private String clientName;
    private String description;
    private Date creationDate;
    private List<FullActorDTO> actors = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<FullActorDTO> getActors() {
        return actors;
    }

    public void setActors(List<FullActorDTO> actors) {
        this.actors = actors;
    }
}
