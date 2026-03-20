package com.menkaix.backlogs.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.Story;

import java.util.Collection;
import java.util.List;

@RepositoryRestController(path = "stories", value = "stories")
public interface StoryRepository extends MongoRepository<Story, String> {

    List<Story> findByActorId(String ref);

    List<Story> findByActorIdOrderByLastUpdateDateDesc(String ref);

    List<Story> findByActorIdIn(Collection<String> actorIds);

}
