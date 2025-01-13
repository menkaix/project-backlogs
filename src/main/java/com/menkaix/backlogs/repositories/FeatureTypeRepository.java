package com.menkaix.backlogs.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.FeatureType;

import java.util.List;

@RepositoryRestController(path = "feature-types", value = "feature-types")
public interface FeatureTypeRepository extends MongoRepository<FeatureType, String> {

	public List<FeatureType> findByName(String lastName);

}
