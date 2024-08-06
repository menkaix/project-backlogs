package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.entities.Raci;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.models.RaciDTO;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.repositories.RaciRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.services.applicatif.DataAccessService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActorService {

    @Autowired
    DataAccessService projectService ;

    @Autowired
    ActorRepisitory actorRepisitory ;

    @Autowired
    StoryRepository storyRepository ;

    @Autowired
    RaciRepository raciRepository ;


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

    public List<Actor> listActors(String project) throws EntityNotFoundException {

        Project prj = projectService.findProject(project) ;
        if(prj == null) throw new EntityNotFoundException("no project found with reference "+project);

        return actorRepisitory.findByProjectName(prj.name) ;

    }

    public RaciDTO addRaci(String project, RaciDTO raciDTO) throws EntityNotFoundException {
        
        Project prj = projectService.findProject(project) ;

        if(prj == null) throw new EntityNotFoundException(project);

        Raci raci = raciRepository.findByprojectID(prj.code);

        if(raci == null){
            raci = new Raci() ;
            raci.setprojectID(prj.code) ;
        }

        raci.setResponsible(merge(raci.getResponsible(), raciDTO.getR()));
        raci.setAccountable(merge( raci.getAccountable(),raciDTO.getA()));
        raci.setConsulted(merge(raci.getConsulted(), raciDTO.getC()));
        raci.setInformed(merge(raci.getInformed(), raciDTO.getI()));

        Raci saved = raciRepository.save(raci) ;

        raciDTO.setProjectCode(prj.code);
        raciDTO.setR(saved.getResponsible());
        raciDTO.setA(saved.getAccountable());
        raciDTO.setC(saved.getConsulted());
        raciDTO.setI(saved.getInformed());

        return raciDTO ;
    }

    private List<String> merge(List<String> a , List<String> b){
        ArrayList<String>ans = new ArrayList<String>() ;
        ans.addAll(a);
        for (String  c : b) {
            if(!ans.contains(c)){
                ans.add(c);
            }
        }
        return ans ;
    }
}
