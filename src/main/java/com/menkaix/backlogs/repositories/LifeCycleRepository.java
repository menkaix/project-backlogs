package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.LifeCycle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LifeCycleRepository extends MongoRepository<LifeCycle, String> {
}