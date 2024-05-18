package com.menkaix.backlogs.controllers;

import java.util.WeakHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.menkaix.backlogs.services.ProjectService;

@Controller
public class IngestionController {
	
	@Autowired
	private ProjectService projectService ;
	
	@PostMapping("/ingest-story/{project}")
	public ResponseEntity<String> ingestStrory(@RequestBody WeakHashMap<String, String> inputData, @PathVariable String project){
		
		String ans = projectService.ingestStory(project, inputData.get("data"));
		
		return new ResponseEntity<>(ans, HttpStatus.OK);
	}

}
