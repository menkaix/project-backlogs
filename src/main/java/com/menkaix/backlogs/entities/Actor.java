package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Actor {

    @Id
    public String id;

    public String name;

    public String projectName ;
}
