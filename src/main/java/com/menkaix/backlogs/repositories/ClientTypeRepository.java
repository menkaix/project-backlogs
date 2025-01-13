package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.ClientType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientTypeRepository extends MongoRepository<ClientType, String> {
}