package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.People;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PeopleRepository extends MongoRepository<People, String> {
}