package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.services.QdrantIndexingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST endpoints for on-demand Qdrant indexing.
 * All operations are asynchronous — the response is returned immediately.
 */
@RestController
@RequestMapping("/api/qdrant/index")
@ConditionalOnProperty(name = "qdrant.enabled", havingValue = "true")
public class QdrantIndexingController {

    private final QdrantIndexingService qdrantIndexingService;

    public QdrantIndexingController(QdrantIndexingService qdrantIndexingService) {
        this.qdrantIndexingService = qdrantIndexingService;
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<Map<String, String>> indexTask(@PathVariable String taskId) {
        qdrantIndexingService.indexTaskContext(taskId);
        return ResponseEntity.accepted().body(Map.of(
                "status", "accepted",
                "taskId", taskId,
                "message", "Task context indexing started"
        ));
    }

    @PostMapping("/issue/{issueId}")
    public ResponseEntity<Map<String, String>> indexIssue(@PathVariable String issueId) {
        qdrantIndexingService.indexIssueContext(issueId);
        return ResponseEntity.accepted().body(Map.of(
                "status", "accepted",
                "issueId", issueId,
                "message", "Issue context indexing started"
        ));
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Map<String, String>> indexProject(@PathVariable String projectId) {
        qdrantIndexingService.indexProjectTree(projectId);
        return ResponseEntity.accepted().body(Map.of(
                "status", "accepted",
                "projectId", projectId,
                "message", "Project tree indexing started"
        ));
    }
}
