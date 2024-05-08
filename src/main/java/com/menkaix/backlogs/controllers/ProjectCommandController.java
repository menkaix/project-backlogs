package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.services.FeatureService;
import com.menkaix.backlogs.services.FeatureTypeService;
import com.menkaix.backlogs.services.ProjectService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-command")
public class ProjectCommandController {

    static Logger logger = LoggerFactory.getLogger(ProjectCommandController.class) ;


    @Autowired
    private FeatureService featureService ;

    @Autowired
    private FeatureTypeService featureTypeService ;

    @Autowired
    private ProjectService projectService ;

    @GetMapping({"/tree/{project}","/{project}/tree"})
    public String tree(@PathVariable("project") String projectRef){

        return projectService.tree(projectRef) ;
    }





}
