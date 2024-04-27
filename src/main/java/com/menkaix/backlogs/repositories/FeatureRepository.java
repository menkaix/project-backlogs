package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.entities.Client;
import com.menkaix.backlogs.entities.Feature;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;

@RepositoryRestController(path = "features",value = "features")
public interface FeatureRepository extends MongoRepository<Feature, String> {

	
	public List<Feature> findByName(String lastName);

}
