package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Diagram extends AbstractEntity {

    @Id
    public String id;

    private String definition;

    public Diagram() {
        super();
    }

    public Diagram(String name) {
        super(name);
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
