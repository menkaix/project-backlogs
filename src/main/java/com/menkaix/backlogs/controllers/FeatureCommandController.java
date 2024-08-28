package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.services.FeatureService;
import com.menkaix.backlogs.services.FeatureTypeService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feature-command")
public class FeatureCommandController {

    static Logger logger = LoggerFactory.getLogger(FeatureCommandController.class) ;


    @Autowired
    private FeatureService featureService ;

    @Autowired
    private FeatureTypeService featureTypeService ;

    @GetMapping("/refresh-types")
    public String refresh(){
        featureTypeService.build();
        return "OK" ;
    }


    @PostMapping("/{story}/add")
    public ResponseEntity<Feature> addFeature(@PathVariable("story")String story, @RequestBody Feature feature){

        Feature ans = null;
        try {
            ans = featureService.addFeatureToStory(story, feature);
            return new ResponseEntity<>(ans, HttpStatus.CREATED) ;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND) ;
        }



    }

    @PostMapping("/{parent}/add-child")
    public ResponseEntity<Feature> addChild(@PathVariable("parent")String parent, @RequestBody Feature feature){
        Feature ans = null;
        try {
            ans = featureService.addToParent(parent, feature);
            return new ResponseEntity<>(ans, HttpStatus.CREATED) ;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND) ;
        }

    }

    @PostMapping("/{parent}/adopt/{child}")
    public ResponseEntity<Feature> setChild(@PathVariable("parent")String parent, @PathVariable("child")String child){
        Feature ans = null;
        try {
            ans = featureService.addToParent(parent, child);
            return new ResponseEntity<>(ans, HttpStatus.CREATED) ;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND) ;
        }



    }

}
