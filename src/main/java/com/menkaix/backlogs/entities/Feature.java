package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Feature {

    @Id
    public String id;

    public String name;

    public String type;

    public String storyId;

    public String parentID;


}
