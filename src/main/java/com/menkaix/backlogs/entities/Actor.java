package com.menkaix.backlogs.entities;

import com.menkaix.backlogs.utilities.values.ActorType;
import org.springframework.data.annotation.Id;

public class Actor {

    @Id
    public String id;

    public String name;

    public String projectName ;

    public ActorType type = ActorType.USER ;
}
