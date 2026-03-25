package com.menkaix.backlogs.models.transients;

/**
 * Environnement d'un projet, embarqué dans le document Project.
 * Exemples de types : développement, qualification, préproduction, production, staging.
 */
public class ProjectEnvironment {

    private String id;
    private String name;
    private String type;
    private String url;
    private String description;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
