package com.menkaix.backlogs.models;

import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.utilities.values.ActorType;

import java.util.ArrayList;
import java.util.List;

public class FullActorDTO {

    public String id;
    public String name ;
    public String projectName ;
    public String description ;
    public ActorType type ;

    public List<FullStoryDTO> stories = new ArrayList<>() ;
}
