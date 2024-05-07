package com.menkaix.backlogs.entities;

import com.menkaix.backlogs.utilities.values.ActorType;
import org.springframework.data.annotation.Id;

public class Actor extends AbstractEntity {

    @Id
    public String id;

    public String projectName ;
    
    public String description ;

    public ActorType type = ActorType.USER ;
}
