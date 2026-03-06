package com.menkaix.backlogs.mcptools;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.services.ProjectManagementService;

@Service
public class ProjectServiceMCPTools {

    private final ProjectManagementService projectService;
    private final Gson gson;

    public ProjectServiceMCPTools(ProjectManagementService projectService, Gson gson) {
        this.projectService = projectService;
        this.gson = gson;
    }

    public Project createProject(String projectJson) {
        if (projectJson == null || projectJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON du projet ne peut pas être vide");
        }
        Project project = gson.fromJson(projectJson, Project.class);
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom du projet est requis");
        }
        if (project.getCode() == null || project.getCode().isBlank()) {
            throw new IllegalArgumentException("Le code du projet est requis");
        }
        return projectService.create(project);
    }

    public Optional<Project> findProjectById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        return projectService.findById(id);
    }

    public Project updateProject(String projectJson) {
        if (projectJson == null || projectJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON du projet ne peut pas être vide");
        }
        Project details = gson.fromJson(projectJson, Project.class);
        if (details.getId() == null || details.getId().isBlank()) {
            throw new IllegalArgumentException("L'ID est requis pour la mise à jour");
        }
        Project existing = projectService.findById(details.getId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + details.getId()));
        if (details.getName() != null) existing.setName(details.getName());
        if (details.getCode() != null) existing.setCode(details.getCode());
        if (details.getDescription() != null) existing.setDescription(details.getDescription());
        if (details.getClientName() != null) existing.setClientName(details.getClientName());
        if (details.getGroup() != null) existing.setGroup(details.getGroup());
        return projectService.update(existing);
    }

    public String deleteProject(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        projectService.findById(id).orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + id));
        projectService.delete(id);
        return "Projet supprimé avec succès: " + id;
    }

    public Page<Project> findAllProjects(Pageable pageable, String search, String filter) {
        return projectService.findAll(pageable, search, filter);
    }

    public Optional<Project> findProjectByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code ne peut pas être vide");
        }
        return projectService.findByProjectCode(code);
    }

    public Optional<Project> findProjectByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        return projectService.findByProjectName(name);
    }
}
