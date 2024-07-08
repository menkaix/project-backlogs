package com.menkaix.backlogs.models;

import java.util.ArrayList;
import java.util.List;

public class FeatureTreeDTO {
	
	public String id;
    public String name ;
    public String description;
    public String parentID ;
    public String type;
    
    public List<FeatureTreeDTO> children = new ArrayList<>();

}
