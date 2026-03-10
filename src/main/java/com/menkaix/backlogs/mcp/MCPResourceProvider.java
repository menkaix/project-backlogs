package com.menkaix.backlogs.mcp;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.services.ProjectManagementService;
import com.menkaix.backlogs.services.TaskService;

@Service
public class MCPResourceProvider {

    private final ProjectManagementService projectService;
    private final TaskService taskService;

    public MCPResourceProvider(ProjectManagementService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    public Map<String, Object> getResource(String uri) {
        try {
            if (uri.startsWith("projects/") || uri.equals("projects")) {
                return handleProjectResource(uri);
            } else if (uri.startsWith("tasks/") || uri.equals("tasks")) {
                return handleTaskResource(uri);
            } else if (uri.startsWith("schemas/")) {
                return handleSchemaResource(uri);
            } else if (uri.startsWith("server/")) {
                return handleServerResource(uri);
            } else if (uri.startsWith("metrics/")) {
                return handleMetricsResource(uri);
            } else {
                return Map.of("error", "Ressource inconnue: " + uri);
            }
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    public Map<String, Object> listResources() {
        return Map.of("resources", Arrays.asList(
                "projects", "projects/{id}", "projects/{code}/tasks",
                "tasks", "tasks/{id}", "tasks/by-tracking-ref/{ref}",
                "tasks/by-status/{status}", "tasks/overdue", "tasks/upcoming",
                "schemas/project", "schemas/task",
                "server/health", "server/info",
                "metrics/projects/count", "metrics/tasks/count"
        ));
    }

    private Map<String, Object> handleProjectResource(String uri) {
        if (uri.equals("projects")) {
            var projects = projectService.findAll(null, null, null);
            return Map.of("projects", projects.getContent(), "totalElements", projects.getTotalElements());
        } else if (uri.startsWith("projects/") && uri.contains("/tasks")) {
            String[] parts = uri.split("/");
            if (parts.length >= 3) {
                String projectId = parts[1];
                var tasks = taskService.findByProjectId(projectId);
                return Map.of("tasks", tasks, "projectId", projectId);
            }
        } else if (uri.startsWith("projects/")) {
            String projectId = uri.substring("projects/".length());
            Optional<Project> project = projectService.findById(projectId);
            return project.isPresent() ? Map.of("project", project.get())
                    : Map.of("error", "Projet non trouvé: " + projectId);
        }
        return Map.of("error", "Ressource projet invalide: " + uri);
    }

    private Map<String, Object> handleTaskResource(String uri) {
        if (uri.equals("tasks")) {
            var tasks = taskService.findAll(null, null, null);
            return Map.of("tasks", tasks.getContent(), "totalElements", tasks.getTotalElements());
        } else if (uri.startsWith("tasks/by-tracking-ref/")) {
            String ref = uri.substring("tasks/by-tracking-ref/".length());
            Optional<Task> task = taskService.findByTrackingReference(ref);
            return task.isPresent() ? Map.of("task", task.get())
                    : Map.of("error", "Tâche non trouvée pour la référence: " + ref);
        } else if (uri.startsWith("tasks/by-status/")) {
            String status = uri.substring("tasks/by-status/".length());
            return Map.of("tasks", taskService.findByStatus(status), "status", status);
        } else if (uri.equals("tasks/overdue")) {
            var tasks = taskService.findOverdueTasks();
            return Map.of("tasks", tasks, "count", tasks.size());
        } else if (uri.equals("tasks/upcoming")) {
            var tasks = taskService.findUpcomingTasks();
            return Map.of("tasks", tasks, "count", tasks.size());
        } else if (uri.startsWith("tasks/")) {
            String taskId = uri.substring("tasks/".length());
            Optional<Task> task = taskService.findById(taskId);
            return task.isPresent() ? Map.of("task", task.get())
                    : Map.of("error", "Tâche non trouvée: " + taskId);
        }
        return Map.of("error", "Ressource tâche invalide: " + uri);
    }

    private Map<String, Object> handleSchemaResource(String uri) {
        if (uri.equals("schemas/project")) return getProjectSchema();
        if (uri.equals("schemas/task")) return getTaskSchema();
        return Map.of("error", "Schéma inconnu: " + uri);
    }

    private Map<String, Object> handleServerResource(String uri) {
        if (uri.equals("server/health")) {
            return Map.of("status", "healthy", "timestamp", new Date());
        } else if (uri.equals("server/info")) {
            return Map.of(
                    "name", "Backlogs MCP Server",
                    "version", "1.0.0",
                    "capabilities", Arrays.asList("projects", "tasks", "persons", "schemas", "metrics")
            );
        }
        return Map.of("error", "Ressource serveur invalide: " + uri);
    }

    private Map<String, Object> handleMetricsResource(String uri) {
        if (uri.equals("metrics/projects/count")) {
            return Map.of("count", projectService.findAll(null, null, null).getTotalElements());
        } else if (uri.equals("metrics/tasks/count")) {
            return Map.of("count", taskService.findAll(null, null, null).getTotalElements());
        }
        return Map.of("error", "Métrique inconnue: " + uri);
    }

    private Map<String, Object> getProjectSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("properties", Map.of(
                "id", Map.of("type", "string"),
                "name", Map.of("type", "string", "description", "Nom du projet"),
                "code", Map.of("type", "string", "description", "Code unique du projet"),
                "description", Map.of("type", "string"),
                "clientName", Map.of("type", "string"),
                "group", Map.of("type", "string")
        ));
        schema.put("required", Arrays.asList("name", "code"));
        return schema;
    }

    private Map<String, Object> getTaskSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", Map.of("type", "string"));
        properties.put("title", Map.of("type", "string"));
        properties.put("description", Map.of("type", "string"));
        properties.put("projectId", Map.of("type", "string"));
        properties.put("status", Map.of("type", "string"));
        properties.put("assignee", Map.of("type", "string"));
        properties.put("estimate", Map.of("type", "string"));
        properties.put("estimatedManHours", Map.of("type", "number", "description", "Estimation en heures homme"));
        properties.put("trackingReference", Map.of("type", "string"));
        properties.put("deadLine", Map.of("type", "string", "format", "date-time"));
        properties.put("doneDate", Map.of("type", "string", "format", "date-time"));
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("title"));
        return schema;
    }
}
