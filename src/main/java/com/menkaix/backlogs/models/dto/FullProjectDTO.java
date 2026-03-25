package com.menkaix.backlogs.models.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.menkaix.backlogs.models.transients.ProjectEnvironment;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.transients.ProjectVersion;
import com.menkaix.backlogs.models.values.ProjectPhase;
import com.menkaix.backlogs.models.values.ProjectState;

public class FullProjectDTO {
    private String id;
    private String name;
    private String code;
    private String clientName;
    private String description;
    private Date creationDate;
    private ProjectPhase phase;
    private ProjectState status;
    private List<FullActorDTO> actors = new ArrayList<>();
    private List<ProjectMember> team = new ArrayList<>();
    private List<ProjectVersion> versions = new ArrayList<>();
    private List<ProjectEnvironment> environments = new ArrayList<>();

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

    public ProjectPhase getPhase() {
        return phase;
    }

    public void setPhase(ProjectPhase phase) {
        this.phase = phase;
    }

    public ProjectState getStatus() {
        return status;
    }

    public void setStatus(ProjectState status) {
        this.status = status;
    }

    public List<FullActorDTO> getActors() {
        return actors;
    }

    public void setActors(List<FullActorDTO> actors) {
        this.actors = actors;
    }

    public List<ProjectMember> getTeam() {
        return team;
    }

    public void setTeam(List<ProjectMember> team) {
        this.team = team;
    }

    public List<ProjectVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ProjectVersion> versions) {
        this.versions = versions;
    }

    public List<ProjectEnvironment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<ProjectEnvironment> environments) {
        this.environments = environments;
    }
}
