package com.menkaix.backlogs.repositories;

import java.util.List;

import com.menkaix.backlogs.entities.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController(path = "projects",value = "projects")
public interface ProjectRepisitory  extends MongoRepository<Project, String>  {

	public List<Project> findByName(String name);
	public List<Project> findByCode(String code);
	public List<Project> findByClientName(String code);
	
}
