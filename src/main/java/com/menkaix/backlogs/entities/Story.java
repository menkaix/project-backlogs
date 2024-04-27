package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Story {

    @Id
    public String id;

    public String actorRef; // projectName/actorName

    public String action ;


    public String objective ;

    public String scenario ;

}
