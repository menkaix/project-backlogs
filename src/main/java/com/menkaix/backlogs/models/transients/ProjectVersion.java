package com.menkaix.backlogs.models.transients;

import java.util.Date;

/**
 * Version d'un projet, embarquée dans le document Project.
 * Une version représente un jalon livrable avec sa date de création
 * (planification) et sa date de mise en service (déploiement effectif).
 */
public class ProjectVersion {

    private String id;
    private String name;
    private Date creationDate;
    private Date deploymentDate;

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Date deploymentDate) {
        this.deploymentDate = deploymentDate;
    }
}
