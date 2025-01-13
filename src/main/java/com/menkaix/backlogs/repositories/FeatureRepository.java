package com.menkaix.backlogs.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.Feature;

import java.util.List;

@RepositoryRestController(path = "features", value = "features")
public interface FeatureRepository extends MongoRepository<Feature, String> {

	public List<Feature> findByStoryId(String feature);

}
