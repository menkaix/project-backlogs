package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.models.FullFeatureDTO;
import com.menkaix.backlogs.models.FullStoryDTO;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.ProjectRepisitory;
import com.menkaix.backlogs.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository ;

    @Autowired
    private ActorRepisitory actorRepisitory ;

    @Autowired
    private ProjectRepisitory projectRepisitory ;

    @Autowired
    private FeatureRepository featureRepository ;

    public FullStoryDTO storyTree(String storyID){

        Story s = storyRepository.findById(storyID).get() ;
        if(s==null) return  null ;

        FullStoryDTO storyDTO = new FullStoryDTO() ;
        storyDTO.id = s.id ;

        storyDTO.action = s.action ;
        storyDTO.objective = s.objective ;
        storyDTO.scenario = s.scenario ;

        Actor a = actorRepisitory.findById(s.actorId).get();
        if(a==null) return  null ;
        storyDTO.actorName = a.name ;


        List<Project> projects = projectRepisitory.findByName(a.projectName);
        if(projects.size()>0){
            storyDTO.projectCode = projects.get(0).code ;
        }

        List<Feature> features = featureRepository.findByStoryId(s.id) ;
        for(Feature f : features){

            FullFeatureDTO fullFeatureDTO = new FullFeatureDTO() ;

            fullFeatureDTO.id=f.id;
            fullFeatureDTO.name =f.name;
            fullFeatureDTO.description=f.description;
            fullFeatureDTO.type=f.type;
            fullFeatureDTO.parentID=f.parentID;

            storyDTO.features.add(fullFeatureDTO);

        }

        return  storyDTO ;
    }

}
