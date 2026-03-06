package com.menkaix.backlogs.mcptools;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.services.TaskService;

@Service
public class TaskServiceMCPTools {

    private final TaskService taskService;
    private final Gson gson;

    public TaskServiceMCPTools(TaskService taskService, Gson gson) {
        this.taskService = taskService;
        this.gson = gson;
    }

    public Task createTask(String taskJson) {
        if (taskJson == null || taskJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de la tâche ne peut pas être vide");
        }
        Task task = gson.fromJson(taskJson, Task.class);
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de la tâche est requis");
        }
        return taskService.create(task);
    }

    public Optional<Task> findTaskById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        return taskService.findById(id);
    }

    public Optional<Task> findTaskByTrackingReference(String ref) {
        if (ref == null || ref.isBlank()) {
            throw new IllegalArgumentException("La référence de suivi ne peut pas être vide");
        }
        return taskService.findByTrackingReference(ref);
    }

    public Task updateTask(String taskJson) {
        if (taskJson == null || taskJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de la tâche ne peut pas être vide");
        }
        Task details = gson.fromJson(taskJson, Task.class);
        if (details.getId() == null || details.getId().isBlank()) {
            throw new IllegalArgumentException("L'ID est requis pour la mise à jour");
        }
        Task existing = taskService.findById(details.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID: " + details.getId()));
        if (details.getTitle() != null) existing.setTitle(details.getTitle());
        if (details.getDescription() != null) existing.setDescription(details.getDescription());
        if (details.getStatus() != null) existing.setStatus(details.getStatus());
        if (details.getAssignee() != null) existing.setAssignee(details.getAssignee());
        if (details.getEstimate() != null) existing.setEstimate(details.getEstimate());
        if (details.getDeadLine() != null) existing.setDeadLine(details.getDeadLine());
        if (details.getDoneDate() != null) existing.setDoneDate(details.getDoneDate());
        return taskService.update(existing);
    }

    public String deleteTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        taskService.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID: " + id));
        taskService.delete(id);
        return "Tâche supprimée avec succès: " + id;
    }

    public Page<Task> findAllTasks(Pageable pageable, String search, String filter) {
        return taskService.findAll(pageable, search, filter);
    }

    public List<Task> findOverdueTasks() {
        return taskService.findOverdueTasks();
    }

    public List<Task> findUpcomingTasks() {
        return taskService.findUpcomingTasks();
    }

    public List<Task> findTasksByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Le statut ne peut pas être vide");
        }
        return taskService.findByStatus(status);
    }

    public List<Task> findTasksByProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("L'ID du projet ne peut pas être vide");
        }
        return taskService.findByProjectId(projectId);
    }
}
