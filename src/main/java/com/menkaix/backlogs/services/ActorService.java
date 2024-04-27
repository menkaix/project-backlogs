package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.utilities.exceptions.ProjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActorService {

    @Autowired
    ProjectService projectService ;

    @Autowired
    ActorRepisitory actorRepisitory ;


    public Actor addNew(String project, Actor actor) throws ProjectNotFoundException {

        Project prj = projectService.findProject(project) ;
        if(prj == null) throw new ProjectNotFoundException("no project foun with reference "+project);

        actor.projectName = prj.name ;

        return  save(actor) ;
    }

    private Actor save(Actor actor) {

        return actorRepisitory.save(actor) ;
    }
}
