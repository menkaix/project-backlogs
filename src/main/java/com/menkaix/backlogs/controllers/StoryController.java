package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.FullStoryDTO;
import com.menkaix.backlogs.services.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("update")
    public ResponseEntity<FullStoryDTO> update(@RequestBody FullStoryDTO storyDTO){

        FullStoryDTO ansStoryDTO = storyService.updateStory(storyDTO) ;

        return new ResponseEntity<>(ansStoryDTO, HttpStatus.OK) ;
    }

}
