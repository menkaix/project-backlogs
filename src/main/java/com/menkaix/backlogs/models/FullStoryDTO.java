package com.menkaix.backlogs.models;

import java.util.ArrayList;
import java.util.List;

public class FullStoryDTO {

    public String id;

    public String actorId;

    public String action ;

    public String objective ;

    public String scenario ;

    public List<FullFeatureDTO> features = new ArrayList<>() ;

}
