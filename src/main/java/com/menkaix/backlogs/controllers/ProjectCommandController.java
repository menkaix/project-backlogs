package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.dto.FeatureTreeDTO;
import com.menkaix.backlogs.models.entities.Actor;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.services.ActorService;
import com.menkaix.backlogs.services.ProjectService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import com.menkaix.backlogs.models.dto.RaciDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-command")
public class ProjectCommandController {

    static Logger logger = LoggerFactory.getLogger(ProjectCommandController.class);

    @Autowired
    ActorService actorService;

    @Autowired
    private ProjectService projectService;

    @GetMapping({ "/{project}/tree" })
    public String tree(@PathVariable("project") String projectRef) {

        return projectService.tree(projectRef);
    }

    @GetMapping({ "/{project}/feature-tree" })
    public ResponseEntity<List<FeatureTreeDTO>> featureTree(@PathVariable("project") String projectRef) {

        List<FeatureTreeDTO> ans = projectService.featureTree(projectRef);

        return new ResponseEntity<List<FeatureTreeDTO>>(ans, HttpStatus.OK);
    }

    @GetMapping({ "/{project}/csv" })
    public String csv(@PathVariable("project") String projectRef) {

        return projectService.csv(projectRef);
    }

    @GetMapping({ "/{project}/csv-tasks" })
    public String csvTasks(@PathVariable("project") String projectRef) {

        return projectService.csvTasks(projectRef);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAll() {

        List<Project> ans = projectService.getAll();

        return new ResponseEntity<>(ans, HttpStatus.OK);
    }

    @PostMapping("/{project}/add-actor")
    public ResponseEntity<Actor> addActor(@PathVariable("project") String project, @RequestBody Actor actor) {

        try {
            Actor ans = actorService.addNew(project, actor);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }

    @PostMapping("/{project}/add-raci")
    public ResponseEntity<RaciDTO> addRaci(@PathVariable("project") String project, @RequestBody RaciDTO raci) {

        try {
            RaciDTO ans = projectService.addRaci(project, raci);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }

    @GetMapping("/{project}/list-actors")
    public ResponseEntity<List<Actor>> listActor(@PathVariable("project") String project) {

        try {
            List<Actor> ans = actorService.listActors(project);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }

}
