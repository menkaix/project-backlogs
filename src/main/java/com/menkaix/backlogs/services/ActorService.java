package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActorService {

    @Autowired
    ProjectService projectService ;

    @Autowired
    ActorRepisitory actorRepisitory ;

    @Autowired
    StoryRepository storyRepository ;


    public Actor addNew(String project, Actor actor) throws EntityNotFoundException {

        Project prj = projectService.findProject(project) ;
        if(prj == null) throw new EntityNotFoundException("no project foun with reference "+project);

        actor.projectName = prj.name ;

        return  save(actor) ;
    }

    private Actor save(Actor actor) {

        return actorRepisitory.save(actor) ;
    }

    public Story addStory(String project, String name, Story story) throws EntityNotFoundException {

        Project prj = projectService.findProject(project) ;
        if(prj == null) throw new EntityNotFoundException("no project found with reference "+project);

        List<Actor> actors = actorRepisitory.findByProjectName(prj.name) ;

        if(actors.size()<=0) throw new EntityNotFoundException("no actor found with name "+name+" in project "+project);

        for (Actor a: actors) {
            if(a.name.equalsIgnoreCase(name)){
                story.actorId = a.id ;
                return storyRepository.save(story) ;
            }
        }

        return null ;
    }
}
