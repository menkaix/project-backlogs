package com.menkaix.backlogs.controllers;


import com.menkaix.backlogs.models.entities.FeatureType;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.repositories.FeatureTypeRepository;
import com.menkaix.backlogs.repositories.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private FeatureTypeRepository typeRepository ;
    
    @Autowired
    private TaskRepository taskRepository ;

   

    @GetMapping("/featuretypes")
    public ResponseEntity<List<FeatureType>> getFeatureTypes(){

        List<FeatureType> ans = typeRepository.findAll() ;

        return  new ResponseEntity<>(ans, HttpStatus.OK) ;

    }
    
    @GetMapping("/normalize-tasks")
    public String normaliezTasks() {
    	
    	List<Task> tasks = taskRepository.findAll() ;
    	
    	for (Task task : tasks) {
			if(task.reference != null && task.reference.split("/").length == 3) {
				task.idReference = task.reference.split("/")[0]+task.reference.split("/")[1] ;

				taskRepository.save(task) ;
			}
		}
    	
    	return "OK" ;
    }

}
