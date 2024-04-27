package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;

@RepositoryRestController(path = "actors",value = "actors")
public interface ActorRepisitory extends MongoRepository<Actor, String>  {

	public List<Actor> findByName(String name);
	public List<Actor> findByProjectName(String name);

	
}
