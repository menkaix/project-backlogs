package com.example.accessingdatamongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController(path = "projects",value = "projects")
public interface ProjectRepisitory  extends MongoRepository<Project, String>  {

	public List<Project> findByName(String name);
	public List<Project> findByCode(String code);
	public List<Project> findByClientName(String code);
	
}
