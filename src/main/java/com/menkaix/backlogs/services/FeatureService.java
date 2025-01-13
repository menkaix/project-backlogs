package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Story;
import com.menkaix.backlogs.repositories.FeatureRepository;

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
        logger.info("Adding feature to story with id: {}", storyId);

        Story story = storyService.findById(storyId).orElseThrow(() -> {
            logger.error("Story not found with id: {}", storyId);
            return new EntityNotFoundException("Story not found with id " + storyId);
        });

        feature.setStoryId(story.getId());
        Feature ans = featureRepository.save(feature);
        taskService.createUsualTasks(ans);

        logger.info("Feature added to story with id: {}", storyId);
        return ans;
    }

    public Feature addToParent(String parentId, Feature feature) throws EntityNotFoundException {
        logger.info("Adding feature to parent with id: {}", parentId);

        Feature parent = featureRepository.findById(parentId).orElseThrow(() -> {
            logger.error("Feature not found with id: {}", parentId);
            return new EntityNotFoundException("Feature not found with id " + parentId);
        });

        feature.setParentID(parent.getId());
        if (feature.getStoryId() == null || feature.getStoryId().isEmpty()) {
            feature.setStoryId(parent.getStoryId());
        }

        Feature savedFeature = featureRepository.save(feature);
        logger.info("Feature added to parent with id: {}", parentId);
        return savedFeature;
    }

    public Feature addToParent(String parentId, String childId) throws EntityNotFoundException {
        logger.info("Adding child feature with id: {} to parent with id: {}", childId, parentId);

        Feature parent = featureRepository.findById(parentId).orElseThrow(() -> {
            logger.error("Feature not found with id: {}", parentId);
            return new EntityNotFoundException("Feature not found with id " + parentId);
        });

        Feature child = featureRepository.findById(childId).orElseThrow(() -> {
            logger.error("Feature not found with id: {}", childId);
            return new EntityNotFoundException("Feature not found with id " + childId);
        });

        child.setParentID(parent.getId());
        if (child.getStoryId() == null || child.getStoryId().isEmpty()) {
            child.setStoryId(parent.getStoryId());
        }

        Feature savedChild = featureRepository.save(child);
        logger.info("Child feature with id: {} added to parent with id: {}", childId, parentId);
        return savedChild;
    }

    public List<Feature> getFeatures(Project prj) {
        logger.info("Getting features for project with id: {}", prj.getId());

        ArrayList<Feature> ans = new ArrayList<>();
        List<Story> stories = storyService.findByProject(prj);

        for (Story story : stories) {
            ans.addAll(featureRepository.findByStoryId(story.getId()));
        }

        logger.info("Features retrieved for project with id: {}", prj.getId());
        return ans;
    }
}
