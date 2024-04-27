package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.services.ActorService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actor-command")
public class ActorCommandController {

    static Logger logger = LoggerFactory.getLogger(ActorCommandController.class) ;

    @Autowired
    ActorService actorService ;


    @GetMapping("/help")
    public String help(){
        return "WIP" ;
    }

    @PostMapping("/{project}/add")
    public ResponseEntity<Actor> addActor(@PathVariable("project")String project, @RequestBody Actor actor){

        try {
            Actor ans = actorService.addNew(project, actor);
            return new ResponseEntity<>(ans, HttpStatus.CREATED) ;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }

    @PostMapping("/{project}/{name}/add-story")
    public ResponseEntity<Story> addStory(@PathVariable("project")String project, @PathVariable("name") String name ,@RequestBody Story story){

        try {
            Story ans = actorService.addStory(project, name, story);
            return new ResponseEntity<>(ans, HttpStatus.CREATED) ;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }



}
