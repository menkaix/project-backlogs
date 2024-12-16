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

    private static Logger logger = LoggerFactory.getLogger(FeatureService.class);
    private final FeatureRepository featureRepository;
    private final StoryService storyService;
    private final TaskService taskService;

    @Autowired
    public FeatureService(FeatureRepository featureRepository, StoryService storyService, TaskService taskService) {
        this.featureRepository = featureRepository;
        this.storyService = storyService;
        this.taskService = taskService;
    }

    public Feature addFeatureToStory(String storyId, Feature feature) throws EntityNotFoundException {

        Story story = storyService.findById(storyId).get();

        if (story == null)
            throw new EntityNotFoundException("story not found with id " + storyId);

        feature.setStoryId(story.getId());

        Feature ans = featureRepository.save(feature);

        taskService.createUsualTasks(ans);

        return ans;
    }

    public Feature addToParent(String parentId, Feature feature) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get();

        if (parent == null)
            throw new EntityNotFoundException("Feature not found with id " + parentId);

        feature.setParentID(parent.getId());

        if (feature.getStoryId() == null || feature.getStoryId().length() == 0) {
            feature.setStoryId(parent.getStoryId());
        }

        return featureRepository.save(feature);
    }

    public Feature addToParent(String parentId, String childId) throws EntityNotFoundException {

        Feature parent = featureRepository.findById(parentId).get();
        Feature child = featureRepository.findById(childId).get();

        if (parent == null)
            throw new EntityNotFoundException("Feature not found with id " + parentId);
        if (child == null)
            throw new EntityNotFoundException("Feature not found with id " + childId);

        child.setParentID(parent.getId());

        if (child.getStoryId() == null || child.getStoryId().length() == 0) {
            child.setStoryId(parent.getStoryId());
        }

        return featureRepository.save(child);

    }

    public List<Feature> getFeatures(Project prj) {

        ArrayList<Feature> ans = new ArrayList<>();

        List<Story> stories = storyService.findByProject(prj);

        for (Story story : stories) {
            ans.addAll(featureRepository.findByStoryId(story.getId()));
        }

        return ans;
    }
}
