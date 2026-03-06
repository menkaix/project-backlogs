package com.menkaix.backlogs.mcptools;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Project;

@Service
public class ProjectToolsRegistry {

    private final ProjectServiceMCPTools tools;
    private final Gson gson;

    public ProjectToolsRegistry(ProjectServiceMCPTools tools, Gson gson) {
        this.tools = tools;
        this.gson = gson;
    }

    @Tool(name = "create-project", description = "Crée un nouveau projet. Champs requis: name (nom), code (code unique). Optionnels: description, clientName, group.")
    public String createProject(String projectJson) {
        try {
            return gson.toJson(tools.createProject(projectJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-project-by-id", description = "Récupère un projet par son identifiant unique.")
    public String findProjectById(String id) {
        try {
            Optional<Project> project = tools.findProjectById(id);
            return project.isPresent() ? gson.toJson(project.get())
                    : gson.toJson(Map.of("message", "Projet non trouvé: " + id));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-project", description = "Met à jour un projet existant. Champ requis: id. Champs modifiables: name, code, description, clientName, group.")
    public String updateProject(String projectDetails) {
        try {
            return gson.toJson(tools.updateProject(projectDetails));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "delete-project", description = "Supprime définitivement un projet par son ID.")
    public String deleteProject(String id) {
        try {
            return gson.toJson(Map.of("message", tools.deleteProject(id)));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-projects", description = "Recherche des projets avec pagination. Paramètres: page (défaut 0), size (défaut 10, max 100), search (optionnel), filter (optionnel, format 'champ:valeur').")
    public String findProjects(int page, int size, String search, String filter) {
        try {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;
            Pageable pageable = PageRequest.of(page, size);
            Page<Project> projects = tools.findAllProjects(pageable, search, filter);
            Map<String, Object> result = new HashMap<>();
            result.put("content", projects.getContent());
            result.put("totalElements", projects.getTotalElements());
            result.put("totalPages", projects.getTotalPages());
            result.put("currentPage", projects.getNumber());
            result.put("size", projects.getSize());
            result.put("hasNext", projects.hasNext());
            result.put("hasPrevious", projects.hasPrevious());
            return gson.toJson(result);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-project-by-code", description = "Récupère un projet par son code unique.")
    public String findProjectByCode(String projectCode) {
        try {
            Optional<Project> project = tools.findProjectByCode(projectCode);
            return project.isPresent() ? gson.toJson(project.get())
                    : gson.toJson(Map.of("message", "Aucun projet avec le code: " + projectCode));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-project-by-name", description = "Récupère un projet par son nom.")
    public String findProjectByName(String projectName) {
        try {
            Optional<Project> project = tools.findProjectByName(projectName);
            return project.isPresent() ? gson.toJson(project.get())
                    : gson.toJson(Map.of("message", "Aucun projet avec le nom: " + projectName));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
