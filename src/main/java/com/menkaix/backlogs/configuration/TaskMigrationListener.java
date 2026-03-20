package com.menkaix.backlogs.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

import com.menkaix.backlogs.models.entities.Task;

@Component
public class TaskMigrationListener extends AbstractMongoEventListener<Task> {

    private static final Logger logger = LoggerFactory.getLogger(TaskMigrationListener.class);

    private final MongoTemplate mongoTemplate;

    public TaskMigrationListener(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAfterConvert(AfterConvertEvent<Task> event) {
        Task task = event.getSource();
        if (task.assignee == null || task.assignee.isBlank()) {
            return;
        }
        logger.info("Migration assignee → assignees pour la tâche {}", task.getId());
        for (String email : task.assignee.split(",")) {
            String trimmed = email.trim();
            if (!trimmed.isEmpty() && !task.getAssignees().contains(trimmed)) {
                task.getAssignees().add(trimmed);
            }
        }
        task.assignee = null;
        mongoTemplate.save(task);
    }
}
