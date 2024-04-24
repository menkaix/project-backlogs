package com.example.accessingdatamongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController(path = "clients",value = "clients")
public interface ClientRepository extends MongoRepository<Client, String> {

	public Client findByFirstName(String firstName);
	public List<Client> findByLastName(String lastName);

}
