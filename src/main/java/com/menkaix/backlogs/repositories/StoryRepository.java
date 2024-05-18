package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.entities.Story;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;

@RepositoryRestController(path = "stories",value = "stories")
public interface StoryRepository extends MongoRepository<Story, String>  {

    List<Story> findByActorId(String ref) ;

}
