package com.menkaix.backlogs.mcptools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Task;

@Service
public class TaskToolsRegistry {

    private final TaskServiceMCPTools tools;
    private final Gson gson;

    public TaskToolsRegistry(TaskServiceMCPTools tools, Gson gson) {
        this.tools = tools;
        this.gson = gson;
    }

    @Tool(name = "create-task", description = "Crée une nouvelle tâche. Champs requis: title (string). Optionnels: projectId, description, status, assignees (liste d'emails), estimate, estimatedManHours (double, heures homme), trackingReference, plannedStart, deadLine (dates ISO-8601).")
    public String createTask(String taskJson) {
        try {
            return gson.toJson(tools.createTask(taskJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-task-by-id", description = "Récupère une tâche par son identifiant unique.")
    public String findTaskById(String id) {
        try {
            Optional<Task> task = tools.findTaskById(id);
            return task.isPresent() ? gson.toJson(task.get())
                    : gson.toJson(Map.of("message", "Tâche non trouvée: " + id));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-task-by-tracking-reference", description = "Récupère une tâche par sa référence de suivi externe.")
    public String findTaskByTrackingReference(String trackingReference) {
        try {
            Optional<Task> task = tools.findTaskByTrackingReference(trackingReference);
            return task.isPresent() ? gson.toJson(task.get())
                    : gson.toJson(Map.of("message", "Tâche non trouvée pour la référence: " + trackingReference));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-task", description = "Met à jour une tâche existante. Champ requis: id. Champs modifiables: title, description, status, assignees (liste d'emails), estimate, estimatedManHours (double, heures homme), deadLine, doneDate.")
    public String updateTask(String taskDetails) {
        try {
            return gson.toJson(tools.updateTask(taskDetails));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "delete-task", description = "Supprime définitivement une tâche par son ID.")
    public String deleteTask(String id) {
        try {
            return gson.toJson(Map.of("message", tools.deleteTask(id)));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-tasks", description = "Recherche des tâches avec pagination. Paramètres: page (défaut 0), size (défaut 10, max 100), search (optionnel), filter (optionnel, format 'champ:valeur').")
    public String findTasks(int page, int size, String search, String filter) {
        try {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;
            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasks = tools.findAllTasks(pageable, search, filter);
            Map<String, Object> result = new HashMap<>();
            result.put("content", tasks.getContent());
            result.put("totalElements", tasks.getTotalElements());
            result.put("totalPages", tasks.getTotalPages());
            result.put("currentPage", tasks.getNumber());
            result.put("size", tasks.getSize());
            result.put("hasNext", tasks.hasNext());
            result.put("hasPrevious", tasks.hasPrevious());
            return gson.toJson(result);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-overdue-tasks", description = "Récupère toutes les tâches en retard (deadline dépassée et non terminées).")
    public String findOverdueTasks() {
        try {
            List<Task> tasks = tools.findOverdueTasks();
            return gson.toJson(Map.of("tasks", tasks, "count", tasks.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-upcoming-tasks", description = "Récupère les tâches avec deadline dans les 7 prochains jours et non terminées.")
    public String findUpcomingTasks() {
        try {
            List<Task> tasks = tools.findUpcomingTasks();
            return gson.toJson(Map.of("tasks", tasks, "count", tasks.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-tasks-by-status", description = "Récupère les tâches par statut (ex: TODO, IN_PROGRESS, DONE, BLOCKED).")
    public String findTasksByStatus(String status) {
        try {
            List<Task> tasks = tools.findTasksByStatus(status);
            return gson.toJson(Map.of("tasks", tasks, "count", tasks.size(), "status", status));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-tasks-by-project", description = "Récupère toutes les tâches d'un projet par son ID.")
    public String findTasksByProject(String projectId) {
        try {
            List<Task> tasks = tools.findTasksByProjectId(projectId);
            return gson.toJson(Map.of("tasks", tasks, "count", tasks.size(), "projectId", projectId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
