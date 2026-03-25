package com.menkaix.backlogs.mcp;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Skill;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.services.PersonService;
import com.menkaix.backlogs.services.ProjectManagementService;
import com.menkaix.backlogs.services.SkillService;
import com.menkaix.backlogs.services.TaskContextService;
import com.menkaix.backlogs.services.TaskService;

@Service
public class MCPResourceProvider {

    private final ProjectManagementService projectService;
    private final TaskService taskService;
    private final PersonService personService;
    private final SkillService skillService;
    private final TaskContextService taskContextService;

    public MCPResourceProvider(
            ProjectManagementService projectService,
            TaskService taskService,
            PersonService personService,
            SkillService skillService,
            TaskContextService taskContextService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.personService = personService;
        this.skillService = skillService;
        this.taskContextService = taskContextService;
    }

    public Map<String, Object> getResource(String uri) {
        try {
            if (uri.startsWith("projects/") || uri.equals("projects")) {
                return handleProjectResource(uri);
            } else if (uri.startsWith("tasks/") || uri.equals("tasks")) {
                return handleTaskResource(uri);
            } else if (uri.startsWith("persons/") || uri.equals("persons")) {
                return handlePersonResource(uri);
            } else if (uri.startsWith("skills/") || uri.equals("skills")) {
                return handleSkillResource(uri);
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
                "tasks/{id}/context",
                "persons", "persons/{id}", "persons/by-email/{email}",
                "skills", "skills/{id}", "skills/by-category/{category}",
                "schemas/project", "schemas/task", "schemas/skill", "schemas/person",
                "server/health", "server/info",
                "metrics/projects/count", "metrics/tasks/count",
                "metrics/persons/count", "metrics/skills/count"
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
        } else if (uri.endsWith("/context")) {
            String taskId = uri.substring("tasks/".length(), uri.length() - "/context".length());
            return Map.of("context", taskContextService.buildContext(taskId));
        } else if (uri.startsWith("tasks/")) {
            String taskId = uri.substring("tasks/".length());
            Optional<Task> task = taskService.findById(taskId);
            return task.isPresent() ? Map.of("task", task.get())
                    : Map.of("error", "Tâche non trouvée: " + taskId);
        }
        return Map.of("error", "Ressource tâche invalide: " + uri);
    }

    private Map<String, Object> handlePersonResource(String uri) {
        if (uri.equals("persons")) {
            var persons = personService.findAll(null, null);
            return Map.of("persons", persons.getContent(), "totalElements", persons.getTotalElements());
        } else if (uri.startsWith("persons/by-email/")) {
            String email = uri.substring("persons/by-email/".length());
            Optional<People> person = personService.findByEmail(email);
            return person.isPresent() ? Map.of("person", person.get())
                    : Map.of("error", "Personne non trouvée pour l'email: " + email);
        } else if (uri.startsWith("persons/")) {
            String personId = uri.substring("persons/".length());
            Optional<People> person = personService.findById(personId);
            return person.isPresent() ? Map.of("person", person.get())
                    : Map.of("error", "Personne non trouvée: " + personId);
        }
        return Map.of("error", "Ressource personne invalide: " + uri);
    }

    private Map<String, Object> handleSkillResource(String uri) {
        if (uri.equals("skills")) {
            var skills = skillService.findAll();
            return Map.of("skills", skills, "count", skills.size());
        } else if (uri.startsWith("skills/by-category/")) {
            String category = uri.substring("skills/by-category/".length());
            var skills = skillService.findByCategory(category);
            return Map.of("skills", skills, "count", skills.size(), "category", category);
        } else if (uri.startsWith("skills/")) {
            String skillId = uri.substring("skills/".length());
            Optional<Skill> skill = skillService.findById(skillId);
            return skill.isPresent() ? Map.of("skill", skill.get())
                    : Map.of("error", "Skill non trouvé: " + skillId);
        }
        return Map.of("error", "Ressource skill invalide: " + uri);
    }

    private Map<String, Object> handleSchemaResource(String uri) {
        if (uri.equals("schemas/project")) return getProjectSchema();
        if (uri.equals("schemas/task")) return getTaskSchema();
        if (uri.equals("schemas/skill")) return getSkillSchema();
        if (uri.equals("schemas/person")) return getPersonSchema();
        return Map.of("error", "Schéma inconnu: " + uri);
    }

    private Map<String, Object> handleServerResource(String uri) {
        if (uri.equals("server/health")) {
            return Map.of("status", "healthy", "timestamp", new Date());
        } else if (uri.equals("server/info")) {
            return Map.of(
                    "name", "Backlogs MCP Server",
                    "version", "1.1.0",
                    "capabilities", Arrays.asList("projects", "tasks", "persons", "skills", "task-context", "schemas", "metrics")
            );
        }
        return Map.of("error", "Ressource serveur invalide: " + uri);
    }

    private Map<String, Object> handleMetricsResource(String uri) {
        if (uri.equals("metrics/projects/count")) {
            return Map.of("count", projectService.findAll(null, null, null).getTotalElements());
        } else if (uri.equals("metrics/tasks/count")) {
            return Map.of("count", taskService.findAll(null, null, null).getTotalElements());
        } else if (uri.equals("metrics/persons/count")) {
            return Map.of("count", personService.findAll(null, null).getTotalElements());
        } else if (uri.equals("metrics/skills/count")) {
            return Map.of("count", skillService.findAll().size());
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
        properties.put("assignees", Map.of("type", "array", "items", Map.of("type", "string"),
                "description", "Liste des emails des personnes assignées"));
        properties.put("estimate", Map.of("type", "string"));
        properties.put("estimatedManHours", Map.of("type", "number", "description", "Estimation en heures homme"));
        properties.put("trackingReference", Map.of("type", "string"));
        properties.put("startDate", Map.of("type", "string", "format", "date-time"));
        properties.put("deadLine", Map.of("type", "string", "format", "date-time"));
        properties.put("doneDate", Map.of("type", "string", "format", "date-time"));
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("title"));
        return schema;
    }

    private Map<String, Object> getSkillSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("properties", Map.of(
                "id", Map.of("type", "string"),
                "name", Map.of("type", "string", "description", "Nom unique du skill"),
                "description", Map.of("type", "string"),
                "category", Map.of("type", "string"),
                "tags", Map.of("type", "array", "items", Map.of("type", "string"))
        ));
        schema.put("required", Arrays.asList("name"));
        return schema;
    }

    private Map<String, Object> getPersonSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", Map.of("type", "string"));
        properties.put("firstName", Map.of("type", "string"));
        properties.put("lastName", Map.of("type", "string"));
        properties.put("email", Map.of("type", "string", "format", "email"));
        properties.put("description", Map.of("type", "string"));
        properties.put("isActive", Map.of("type", "boolean"));
        properties.put("skills", Map.of("type", "array", "description", "Liste des skills avec niveaux (PersonSkill)"));
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("firstName", "lastName", "email"));
        return schema;
    }
}
