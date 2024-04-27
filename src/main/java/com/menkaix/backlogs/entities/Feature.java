package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

import java.util.WeakHashMap;

public class Feature {

    @Id
    public String id;

    public String name;

    public String description;

    public String type;

    public String storyId;

    public String parentID;

    public WeakHashMap<String, String> properties = new WeakHashMap<>() ;


}
