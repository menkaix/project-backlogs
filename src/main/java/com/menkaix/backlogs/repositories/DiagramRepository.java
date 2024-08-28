package com.menkaix.backlogs.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.entities.Diagram;

@RepositoryRestController(path = "diagrams",value = "diagrams")
public interface DiagramRepository extends MongoRepository<Diagram, String>{
    public List<Diagram> findByName(String name) ;
    public List<Diagram> findByDefinition(String definition) ;
    
}
