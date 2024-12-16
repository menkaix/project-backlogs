package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.entities.Actor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;

@RepositoryRestController(path = "actors", value = "actors")
public interface ActorRepository extends MongoRepository<Actor, String> {

	public List<Actor> findByName(String name);

	public List<Actor> findByProjectName(String name);

}
