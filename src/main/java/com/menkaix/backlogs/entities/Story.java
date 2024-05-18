package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Story  extends AbstractEntity {

    @Id
    public String id;

    public String actorId;

    public String action ;

    public String objective ;

    public String scenario ;

}
