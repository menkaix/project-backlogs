package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.FeatureType;
import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {

    private static Logger logger = LoggerFactory.getLogger(FeatureService.class) ;

    @Autowired
    private  FeatureRepository featureRepository ;
    
    
    @Autowired
    private StoryService storyService ;

    @Autowired
	private TaskService taskService ;
    
    

	public Feature addFeatureToStory(String storyId, Feature feature) throws EntityNotFoundException {

        Story story = storyService.findById(storyId).get();
        

        if(story == null) throw new EntityNotFoundException("story not found with id "+storyId);
        
        feature.storyId = story.id ;
        
        Feature ans =  featureRepository.save(feature) ;
        
        taskService.createUsualTasks(ans);

        return  ans ;
    }

    public Feature addToParent(String parentId, Feature feature) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get() ;

        if(parent ==  null) throw new EntityNotFoundException("Feature not found with id "+parentId);

        feature.parentID = parent.id ;
        
        if(feature.storyId == null || feature.storyId.length()==0) {
        	feature.storyId = parent.storyId ;
        }

        return  featureRepository.save(feature) ;
    }

    public Feature addToParent(String parentId, String childId) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get() ;
        Feature child = featureRepository.findById(childId).get() ;

        if(parent ==  null) throw new EntityNotFoundException("Feature not found with id "+parentId);
        if(child ==  null) throw new EntityNotFoundException("Feature not found with id "+childId);

        child.parentID = parent.id ;
        
        if(child.storyId == null || child.storyId.length()==0) {
        	child.storyId = parent.storyId ;
        }

        return  featureRepository.save(child) ;

    }

	public List<Feature> getFeatures(Project prj) {
		
		ArrayList<Feature> ans = new ArrayList<>() ;
		
		List<Story> stories = storyService.findByProject(prj);
		
		for (Story story : stories) {
			ans.addAll(featureRepository.findByStoryId(story.id)) ;
		}
		
		return ans;
	}
}
