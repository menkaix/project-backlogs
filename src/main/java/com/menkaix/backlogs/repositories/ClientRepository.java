package com.menkaix.backlogs.repositories;

import java.util.List;

import com.menkaix.backlogs.entities.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController(path = "clients",value = "clients")
public interface ClientRepository extends MongoRepository<Client, String> {

	
	public List<Client> findByName(String lastName);

}
