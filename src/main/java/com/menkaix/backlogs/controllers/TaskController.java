package com.menkaix.backlogs.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.services.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.create(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable String id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable String id, @RequestBody Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskService.update(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Task>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter) {
        return ResponseEntity.ok(taskService.findAll(PageRequest.of(page, size), search, filter));
    }

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<Task>> findByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(taskService.findByProjectId(projectId));
    }

    /**
     * Retourne toutes les tâches d'un projet (directes + indirectes via features).
     * Accepte le nom, le code ou l'id MongoDB du projet.
     */
    @GetMapping("/by-project-ref/{projectRef}")
    public ResponseEntity<List<Task>> findByProjectRef(@PathVariable String projectRef) {
        return ResponseEntity.ok(taskService.findByProjectRef(projectRef));
    }

    @GetMapping("/by-feature/{featureId}")
    public ResponseEntity<List<Task>> findByFeature(@PathVariable String featureId) {
        return ResponseEntity.ok(taskService.findByFeatureId(featureId));
    }

    @GetMapping("/by-story/{storyId}")
    public ResponseEntity<List<Task>> findByStory(@PathVariable String storyId) {
        return ResponseEntity.ok(taskService.findByStoryId(storyId));
    }

    @GetMapping("/by-actor/{actorId}")
    public ResponseEntity<List<Task>> findByActor(@PathVariable String actorId) {
        return ResponseEntity.ok(taskService.findByActorId(actorId));
    }

    @GetMapping("/by-assignee/{email}")
    public ResponseEntity<List<Task>> findByAssignee(@PathVariable String email) {
        return ResponseEntity.ok(taskService.findByAssigneeEmail(email));
    }

    @PostMapping("/{id}/assignees/{email}")
    public ResponseEntity<Task> assignPerson(@PathVariable String id, @PathVariable String email) {
        return ResponseEntity.ok(taskService.assignPerson(id, email));
    }

    @DeleteMapping("/{id}/assignees/{email}")
    public ResponseEntity<Task> unassignPerson(@PathVariable String id, @PathVariable String email) {
        return ResponseEntity.ok(taskService.unassignPerson(id, email));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Task>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.findByStatus(status));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> findOverdue() {
        return ResponseEntity.ok(taskService.findOverdueTasks());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Task>> findUpcoming() {
        return ResponseEntity.ok(taskService.findUpcomingTasks());
    }

    @GetMapping("/by-tracking-ref/{ref}")
    public ResponseEntity<Task> findByTrackingRef(@PathVariable String ref) {
        return taskService.findByTrackingReference(ref)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
