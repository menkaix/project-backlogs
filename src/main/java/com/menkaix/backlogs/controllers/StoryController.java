package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.FullStoryDTO;
import com.menkaix.backlogs.services.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/story-command")
public class StoryController {

    @Autowired
    private StoryService storyService ;

    @GetMapping("{storyID}/tree")
    public ResponseEntity<FullStoryDTO> storyTree(@PathVariable("storyID") String storyID){

        FullStoryDTO storyDTO = storyService.storyTree(storyID) ;

        return new ResponseEntity<>(storyDTO, HttpStatus.OK) ;
    }

}
