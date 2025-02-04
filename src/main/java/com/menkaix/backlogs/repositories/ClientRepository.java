package com.menkaix.backlogs.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.Client;

@RepositoryRestController(path = "clients",value = "clients")
public interface ClientRepository extends MongoRepository<Client, String> {

	
	public List<Client> findByName(String lastName);

}
