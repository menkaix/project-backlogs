package com.menkaix.backlogs.mcptools;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.values.ProjectPhase;
import com.menkaix.backlogs.models.values.ProjectState;
import com.menkaix.backlogs.services.ProjectManagementService;
import com.menkaix.backlogs.services.ProjectService;

@Service
public class ProjectServiceMCPTools {

    private final ProjectManagementService projectManagementService;
    private final ProjectService projectService;
    private final Gson gson;

    public ProjectServiceMCPTools(ProjectManagementService projectManagementService, ProjectService projectService, Gson gson) {
        this.projectManagementService = projectManagementService;
        this.projectService = projectService;
        this.gson = gson;
    }

    // ─── CRUD de base ────────────────────────────────────────────────────────────

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
        return projectManagementService.create(project);
    }

    public Optional<Project> findProjectById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        return projectManagementService.findById(id);
    }

    public Project updateProject(String projectJson) {
        if (projectJson == null || projectJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON du projet ne peut pas être vide");
        }
        Project details = gson.fromJson(projectJson, Project.class);
        if (details.getId() == null || details.getId().isBlank()) {
            throw new IllegalArgumentException("L'ID est requis pour la mise à jour");
        }
        Project existing = projectManagementService.findById(details.getId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + details.getId()));
        if (details.getName() != null) existing.setName(details.getName());
        if (details.getCode() != null) existing.setCode(details.getCode());
        if (details.getDescription() != null) existing.setDescription(details.getDescription());
        if (details.getClientName() != null) existing.setClientName(details.getClientName());
        if (details.getGroup() != null) existing.setGroup(details.getGroup());
        return projectManagementService.update(existing);
    }

    public String deleteProject(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        projectManagementService.findById(id).orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + id));
        projectManagementService.delete(id);
        return "Projet supprimé avec succès: " + id;
    }

    public Page<Project> findAllProjects(Pageable pageable, String search, String filter) {
        return projectManagementService.findAll(pageable, search, filter);
    }

    public Optional<Project> findProjectByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code ne peut pas être vide");
        }
        return projectManagementService.findByProjectCode(code);
    }

    public Optional<Project> findProjectByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        return projectManagementService.findByProjectName(name);
    }

    // ─── Phase et état ───────────────────────────────────────────────────────────

    public Project updateProjectPhase(String projectRef, String phaseName) {
        if (projectRef == null || projectRef.isBlank()) {
            throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        }
        if (phaseName == null || phaseName.isBlank()) {
            throw new IllegalArgumentException("La phase ne peut pas être vide");
        }
        try {
            ProjectPhase phase = ProjectPhase.valueOf(phaseName.trim().toUpperCase());
            return projectService.updatePhase(projectRef, phase);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Phase inconnue: " + phaseName +
                    ". Valeurs valides: INCONNUE, AVANT_VENTE, CADRAGE, CONCEPTION, PREPRODUCTION, PRODUCTION, MAINTENANCE");
        }
    }

    public List<Project> findProjectsByState(String stateName) {
        if (stateName == null || stateName.isBlank()) {
            throw new IllegalArgumentException("L'état ne peut pas être vide");
        }
        try {
            ProjectState state = ProjectState.valueOf(stateName.trim().toUpperCase());
            return projectService.getByState(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("État inconnu: " + stateName +
                    ". Valeurs valides: ACTIVE, STANDBY, CLOSED");
        }
    }

    // ─── Équipe projet ───────────────────────────────────────────────────────────

    public List<ProjectMember> getProjectTeam(String projectRef) {
        if (projectRef == null || projectRef.isBlank()) {
            throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        }
        return projectService.getTeam(projectRef);
    }

    public Project addProjectTeamMember(String projectRef, String personId) {
        if (projectRef == null || projectRef.isBlank()) {
            throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        }
        if (personId == null || personId.isBlank()) {
            throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        }
        try {
            return projectService.addTeamMember(projectRef, personId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Project removeProjectTeamMember(String projectRef, String personId) {
        if (projectRef == null || projectRef.isBlank()) {
            throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        }
        if (personId == null || personId.isBlank()) {
            throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        }
        try {
            return projectService.removeTeamMember(projectRef, personId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Project refreshProjectTeamMemberSkills(String projectRef, String personId) {
        if (projectRef == null || projectRef.isBlank()) {
            throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        }
        if (personId == null || personId.isBlank()) {
            throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        }
        try {
            return projectService.refreshTeamMemberSkills(projectRef, personId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
