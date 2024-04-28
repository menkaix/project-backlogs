package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {

    private static Logger logger = LoggerFactory.getLogger(FeatureService.class) ;

    @Autowired
    private  FeatureRepository featureRepository ;

    //TODO change to service
    @Autowired
    private StoryRepository storyRepository ;

    public Feature addToStory(String storyId, Feature feature) throws EntityNotFoundException {

        Story story = storyRepository.findById(storyId).get();

        if(story == null) throw new EntityNotFoundException("story not found with id "+storyId);

        feature.storyId = story.id ;

        return  featureRepository.save(feature) ;
    }

    public Feature addToParent(String parentId, Feature feature) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get() ;

        if(parent ==  null) throw new EntityNotFoundException("Feature not found with id "+parentId);

        feature.parentID = parent.id ;

        return  featureRepository.save(feature) ;
    }

    public Feature addToParent(String parentId, String childId) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get() ;
        Feature child = featureRepository.findById(childId).get() ;

        if(parent ==  null) throw new EntityNotFoundException("Feature not found with id "+parentId);
        if(child ==  null) throw new EntityNotFoundException("Feature not found with id "+childId);

        child.parentID = parent.id ;

        return  featureRepository.save(child) ;

    }
}
