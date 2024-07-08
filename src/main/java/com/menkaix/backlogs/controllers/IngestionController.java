package com.menkaix.backlogs.controllers;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.menkaix.backlogs.services.ProjectService;

@Deprecated
@Controller
public class IngestionController {

	static Logger logger = LoggerFactory.getLogger(IngestionController.class) ;
	
	@Autowired
	private ProjectService projectService ;
	
	@PostMapping("/ingest-story/{project}")
	public ResponseEntity<String> ingestStrory(@RequestBody Map<String, Object> inputData, @PathVariable String project){

		String ans = "" ;

		if(inputData.keySet().contains("data")){
			ans = projectService.ingestStory(project, inputData.get("data").toString());
		}
		else if(inputData.keySet().contains("batch")){

			logger.info("batch");

			List<String> data = (List<String>)inputData.get("batch");

			for (String elt: data) {

				String stringElt = elt ;

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				logger.info("batch: "+stringElt);

				ans += projectService.ingestStory(project, stringElt) +"\n";
			}

		}

		
		return new ResponseEntity<>(ans, HttpStatus.OK);
	}

}
