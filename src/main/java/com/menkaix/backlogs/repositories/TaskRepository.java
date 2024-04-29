package com.menkaix.backlogs.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.entities.Task;

@RepositoryRestController(path = "tasks",value = "tasks")
public interface TaskRepository extends MongoRepository<Task, String>  {

    List<Task> findByProjectId(String project) ;
    
    //WTF ?

}
