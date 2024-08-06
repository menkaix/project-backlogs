package com.menkaix.backlogs.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.entities.Raci;

@RepositoryRestController(path = "racies",value = "racies")
public interface RaciRepository   extends MongoRepository<Raci, String> {

    public Raci findByprojectID(String projectID);
    
}
