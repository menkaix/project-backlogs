package com.menkaix.backlogs.repositories;

import java.util.Optional;

import com.menkaix.backlogs.models.entities.People;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PeopleRepository extends MongoRepository<People, String> {

    Optional<People> findByEmail(String email);
    Optional<People> findByEmailAndIsActive(String email, boolean isActive);
}