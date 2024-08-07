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
import com.menkaix.backlogs.services.applicatif.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository ;

    @Autowired
    private ActorRepisitory actorRepisitory ;

    @Autowired
    private DataAccessService projectRepisitory ;

    @Autowired
    private FeatureRepository featureRepository ;

    @Autowired
    private  DataAccessService projectService ;

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

        List<Project> projects = projectRepisitory.findProjectByName(a.projectName);
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

    public FullStoryDTO updateStory(FullStoryDTO storyDTO) {

        Story story = storyRepository.findById(storyDTO.id).orElse(new Story()) ;

        Project project = projectService.findProject(storyDTO.projectCode);

        if(project==null) return  null ;

        List<Actor> actors = actorRepisitory.findByProjectName(project.name) ;

        for (Actor actor:actors) {
            if(actor.name.equalsIgnoreCase(storyDTO.actorName)){
                story.actorId = actor.id;
            }
        }

        story.objective = storyDTO.objective ;
        story.action = storyDTO.action ;
        story.scenario = storyDTO.scenario ;

        story.lastUpdateDate = new Date() ;

        storyRepository.save(story) ;

        storyDTO.id = story.id ;

        return  storyDTO ;
    }

	public Optional<Story> findById(String storyId) {
		
		return storyRepository.findById(storyId);
	}

	public List<Story> findByProject(Project prj) {
		
		ArrayList<Story> ans = new ArrayList<>() ;
		
		List<Actor> actors = actorRepisitory.findByProjectName(prj.name);
		
		for (Actor actor : actors) {
			ans.addAll(storyRepository.findByActorId(actor.id));
		}
		
		return ans;
	}
}
