package com.menkaix.backlogs.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.Project;

@RepositoryRestController(path = "projects", value = "projects")
public interface ProjectRepository extends MongoRepository<Project, String> {

	public List<Project> findByName(String name);

	public List<Project> findByGroup(String name);

	public List<Project> findByCode(String code);

	public List<Project> findByClientName(String code);

}
