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
