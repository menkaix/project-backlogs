package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class FeatureType {

    @Id
    public String id;

    public String name ;

    public ArrayList<String> keyFeatures = new ArrayList<>() ;

    public boolean isContainer = false ;

}
