package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.dto.FullFeatureDTO;
import com.menkaix.backlogs.models.dto.FullStoryDTO;
import com.menkaix.backlogs.models.entities.Actor;
import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Story;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.services.applicatif.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final ActorRepository actorRepository;
    private final DataAccessService projectRepository;
    private final FeatureRepository featureRepository;
    private final DataAccessService projectService;

    @Autowired
    public StoryService(StoryRepository storyRepository, ActorRepository actorRepository,
            DataAccessService projectRepository, FeatureRepository featureRepository,
            DataAccessService projectService) {
        this.storyRepository = storyRepository;
        this.actorRepository = actorRepository;
        this.projectRepository = projectRepository;
        this.featureRepository = featureRepository;
        this.projectService = projectService;
    }

    public StoryRepository getStoryRepository() {
        return storyRepository;
    }

    public ActorRepository getActorRepository() {
        return actorRepository;
    }

    public DataAccessService getProjectRepository() {
        return projectRepository;
    }

    public FeatureRepository getFeatureRepository() {
        return featureRepository;
    }

    public DataAccessService getProjectService() {
        return projectService;
    }

    public FullStoryDTO storyTree(String storyID) {

        Story s = storyRepository.findById(storyID).get();
        if (s == null)
            return null;

        FullStoryDTO storyDTO = new FullStoryDTO();
        storyDTO.setId(s.getId());

        storyDTO.setAction(s.getAction());
        storyDTO.setObjective(s.getObjective());
        storyDTO.setScenario(s.getScenario());

        Actor a = actorRepository.findById(s.getActorId()).get();
        if (a == null)
            return null;
        storyDTO.setActorName(a.getName());

        List<Project> projects = projectRepository.findProjectByName(a.getProjectName());
        if (projects.size() > 0) {
            storyDTO.setProjectCode(projects.get(0).getCode());
        }

        List<Feature> features = featureRepository.findByStoryId(s.getId());
        for (Feature f : features) {

            FullFeatureDTO fullFeatureDTO = new FullFeatureDTO();

            fullFeatureDTO.setId(f.getId());
            fullFeatureDTO.setName(f.getName());
            fullFeatureDTO.setDescription(f.getDescription());
            fullFeatureDTO.setType(f.getType());
            fullFeatureDTO.setParentID(f.getParentID());

            storyDTO.getFeatures().add(fullFeatureDTO);

        }

        return storyDTO;
    }

    public FullStoryDTO updateStory(FullStoryDTO storyDTO) {

        Story story = storyRepository.findById(storyDTO.getId()).orElse(new Story());

        Project project = projectService.findProject(storyDTO.getProjectCode());

        if (project == null)
            return null;

        List<Actor> actors = actorRepository.findByProjectName(project.getName());

        for (Actor actor : actors) {
            if (actor.getName().equalsIgnoreCase(storyDTO.getActorName())) {
                story.setActorId(actor.getId());
            }
        }

        story.setObjective(storyDTO.getObjective());
        story.setAction(storyDTO.getAction());
        story.setScenario(storyDTO.getScenario());

        story.setLastUpdateDate(new Date());

        storyRepository.save(story);

        storyDTO.setId(story.getId());

        return storyDTO;
    }

    public Optional<Story> findById(String storyId) {

        return storyRepository.findById(storyId);
    }

    public List<Story> findByProject(Project prj) {

        ArrayList<Story> ans = new ArrayList<>();

        List<Actor> actors = actorRepository.findByProjectName(prj.getName());

        for (Actor actor : actors) {
            ans.addAll(storyRepository.findByActorId(actor.getId()));
        }

        return ans;
    }
}
