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
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.transients.ProjectEnvironment;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.transients.ProjectVersion;

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

    @Tool(name = "update-project-phase", description = "Met à jour la phase d'un projet. Phases valides: INCONNUE, AVANT_VENTE, CADRAGE, CONCEPTION, PREPRODUCTION, PRODUCTION, MAINTENANCE. La référence peut être le nom, le code ou l'ID MongoDB.")
    public String updateProjectPhase(String projectRef, String phaseName) {
        try {
            return gson.toJson(tools.updateProjectPhase(projectRef, phaseName));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-projects-by-state", description = "Récupère les projets selon leur état calculé à partir des tâches. États valides: ACTIVE (tâches en cours), STANDBY (tâches en attente), CLOSED (toutes les tâches terminées).")
    public String findProjectsByState(String stateName) {
        try {
            List<Project> projects = tools.findProjectsByState(stateName);
            return gson.toJson(Map.of("projects", projects, "count", projects.size(), "state", stateName));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "get-project-team", description = "Récupère l'équipe d'un projet avec les skillsets de chaque membre. La référence peut être le nom, le code ou l'ID MongoDB.")
    public String getProjectTeam(String projectRef) {
        try {
            List<ProjectMember> team = tools.getProjectTeam(projectRef);
            return gson.toJson(Map.of("team", team, "count", team.size(), "projectRef", projectRef));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "add-project-team-member", description = "Ajoute une personne à l'équipe d'un projet (copie son skillset actuel). La référence projet peut être le nom, le code ou l'ID MongoDB.")
    public String addProjectTeamMember(String projectRef, String personId) {
        try {
            return gson.toJson(tools.addProjectTeamMember(projectRef, personId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "remove-project-team-member", description = "Retire une personne de l'équipe d'un projet. La référence projet peut être le nom, le code ou l'ID MongoDB.")
    public String removeProjectTeamMember(String projectRef, String personId) {
        try {
            return gson.toJson(tools.removeProjectTeamMember(projectRef, personId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "refresh-project-team-member-skills", description = "Resynchronise le skillset d'un membre de l'équipe projet depuis son profil People. À utiliser après une mise à jour des compétences d'une personne.")
    public String refreshProjectTeamMemberSkills(String projectRef, String personId) {
        try {
            return gson.toJson(tools.refreshProjectTeamMemberSkills(projectRef, personId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    // ─── Versions projet ─────────────────────────────────────────────────────────

    @Tool(name = "get-project-versions", description = "Retourne la liste des versions d'un projet. La référence peut être le nom, le code ou l'ID MongoDB.")
    public String getProjectVersions(String projectRef) {
        try {
            List<ProjectVersion> versions = tools.getProjectVersions(projectRef);
            return gson.toJson(Map.of("versions", versions, "count", versions.size(), "projectRef", projectRef));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "add-project-version", description = "Ajoute une version au projet. Champs du JSON: name (requis), creationDate (date de création, ISO 8601), deploymentDate (date de mise en service, ISO 8601).")
    public String addProjectVersion(String projectRef, String versionJson) {
        try {
            return gson.toJson(tools.addProjectVersion(projectRef, versionJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-project-version", description = "Met à jour une version existante d'un projet. Champs modifiables: name, creationDate, deploymentDate.")
    public String updateProjectVersion(String projectRef, String versionId, String patchJson) {
        try {
            return gson.toJson(tools.updateProjectVersion(projectRef, versionId, patchJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "remove-project-version", description = "Supprime une version d'un projet par son ID.")
    public String removeProjectVersion(String projectRef, String versionId) {
        try {
            return gson.toJson(tools.removeProjectVersion(projectRef, versionId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    // ─── Environnements projet ───────────────────────────────────────────────────

    @Tool(name = "get-project-environments", description = "Retourne la liste des environnements d'un projet (développement, qualification, préproduction, production, etc.).")
    public String getProjectEnvironments(String projectRef) {
        try {
            List<ProjectEnvironment> envs = tools.getProjectEnvironments(projectRef);
            return gson.toJson(Map.of("environments", envs, "count", envs.size(), "projectRef", projectRef));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "add-project-environment", description = "Ajoute un environnement au projet. Champs du JSON: name (requis), type (ex: développement, qualification, préproduction, production), url, description.")
    public String addProjectEnvironment(String projectRef, String environmentJson) {
        try {
            return gson.toJson(tools.addProjectEnvironment(projectRef, environmentJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-project-environment", description = "Met à jour un environnement existant d'un projet. Champs modifiables: name, type, url, description.")
    public String updateProjectEnvironment(String projectRef, String environmentId, String patchJson) {
        try {
            return gson.toJson(tools.updateProjectEnvironment(projectRef, environmentId, patchJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "remove-project-environment", description = "Supprime un environnement d'un projet par son ID.")
    public String removeProjectEnvironment(String projectRef, String environmentId) {
        try {
            return gson.toJson(tools.removeProjectEnvironment(projectRef, environmentId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
