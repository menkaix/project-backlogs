package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {
}