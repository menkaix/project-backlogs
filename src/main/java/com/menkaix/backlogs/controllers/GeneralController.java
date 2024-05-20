package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.entities.FeatureType;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.FeatureTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private FeatureTypeRepository typeRepository ;

    @GetMapping("/featuretypes")
    public ResponseEntity<List<FeatureType>> getFeatureTypes(){

        List<FeatureType> ans = typeRepository.findAll() ;

        return  new ResponseEntity<>(ans, HttpStatus.OK) ;

    }

}
