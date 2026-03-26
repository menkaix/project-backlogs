package com.menkaix.backlogs.mcptools;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Issue;
import com.menkaix.backlogs.models.values.IssueSeverity;
import com.menkaix.backlogs.models.values.IssueStatus;
import com.menkaix.backlogs.models.values.IssueType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IssueToolsRegistry {

    private final IssueServiceMCPTools tools;
    private final Gson gson;

    public IssueToolsRegistry(IssueServiceMCPTools tools, Gson gson) {
        this.tools = tools;
        this.gson = gson;
    }

    @Tool(name = "create-issue", description = "Crée une nouvelle issue (bug, incident, retour utilisateur, etc.). Champs requis: title. Clés importantes: type (BUG, REGRESSION, VULNERABILITY, INCIDENT, PERFORMANCE, FEATURE_REQUEST, IMPROVEMENT, QUESTION, SUPPORT), severity (CRITICAL, HIGH, MEDIUM, LOW, INFO), reporter, projectId, environment, platform, component, affectedVersion, reproductionSteps, expectedBehavior, actualBehavior.")
    public String createIssue(String issueJson) {
        try {
            return gson.toJson(tools.createIssue(issueJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issue-by-id", description = "Récupère une issue par son identifiant unique.")
    public String findIssueById(String id) {
        try {
            Optional<Issue> issue = tools.findIssueById(id);
            return issue.isPresent() ? gson.toJson(issue.get())
                    : gson.toJson(Map.of("message", "Issue non trouvée : " + id));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issue-by-tracking-reference", description = "Récupère une issue par sa référence de suivi externe (Jira, GitHub, PagerDuty…).")
    public String findIssueByTrackingReference(String ref) {
        try {
            Optional<Issue> issue = tools.findIssueByTrackingReference(ref);
            return issue.isPresent() ? gson.toJson(issue.get())
                    : gson.toJson(Map.of("message", "Issue non trouvée pour la référence : " + ref));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-issue", description = "Met à jour une issue existante. Champ requis: id. Tous les autres champs sont optionnels (mise à jour partielle).")
    public String updateIssue(String issueJson) {
        try {
            return gson.toJson(tools.updateIssue(issueJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "delete-issue", description = "Supprime définitivement une issue par son ID.")
    public String deleteIssue(String id) {
        try {
            return gson.toJson(Map.of("message", tools.deleteIssue(id)));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-issue-status", description = "Met à jour le statut d'une issue. Statuts valides: OPEN, TRIAGED, IN_PROGRESS, IN_REVIEW, RESOLVED, CLOSED, WONT_FIX, DUPLICATE, NEED_MORE_INFO, REOPENED.")
    public String updateIssueStatus(String issueId, String status) {
        try {
            return gson.toJson(tools.updateIssueStatus(issueId, status));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "assign-issue", description = "Assigne une personne à une issue par son email.")
    public String assignIssue(String issueId, String email) {
        try {
            return gson.toJson(tools.assignPerson(issueId, email));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "unassign-issue", description = "Retire l'assignation d'une personne d'une issue.")
    public String unassignIssue(String issueId, String email) {
        try {
            return gson.toJson(tools.unassignPerson(issueId, email));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "get-issue-context", description = "Construit le contexte complet d'une issue pour un LLM: issue (avec type, sévérité, reproduction steps…), projet, feature, user story, actor, issues sœurs et équipe avec skillsets. Utile pour analyser l'impact, suggérer une assignation, générer une description de correctif ou des critères de résolution.")
    public String getIssueContext(String issueId) {
        try {
            return gson.toJson(tools.buildIssueContext(issueId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "assign-issue-to-project", description = "Affecte une issue à un projet en définissant son projectId. L'issue et le projet doivent exister.")
    public String assignIssueToProject(String issueId, String projectId) {
        try {
            return gson.toJson(tools.assignToProject(issueId, projectId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "assign-issue-to-feature", description = "Affecte une issue à une feature en définissant son idReference. L'issue et la feature doivent exister.")
    public String assignIssueToFeature(String issueId, String featureId) {
        try {
            return gson.toJson(tools.assignToFeature(issueId, featureId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues", description = "Recherche des issues avec pagination. Paramètres: page (défaut 0), size (défaut 10, max 100), search (cherche dans title/description/component), filter (format 'champ:valeur').")
    public String findIssues(int page, int size, String search, String filter) {
        try {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;
            Pageable pageable = PageRequest.of(page, size);
            Page<Issue> issues = tools.findAllIssues(pageable, search, filter);
            Map<String, Object> result = new HashMap<>();
            result.put("content", issues.getContent());
            result.put("totalElements", issues.getTotalElements());
            result.put("totalPages", issues.getTotalPages());
            result.put("currentPage", issues.getNumber());
            result.put("hasNext", issues.hasNext());
            return gson.toJson(result);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-project-ref", description = "Récupère toutes les issues d'un projet (directes + via features). La référence peut être le nom, le code ou l'ID MongoDB.")
    public String findIssuesByProjectRef(String projectRef) {
        try {
            List<Issue> issues = tools.findByProjectRef(projectRef);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "projectRef", projectRef));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-feature", description = "Récupère toutes les issues attachées à une feature.")
    public String findIssuesByFeature(String featureId) {
        try {
            List<Issue> issues = tools.findByFeatureId(featureId);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "featureId", featureId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-status", description = "Récupère les issues par statut.")
    public String findIssuesByStatus(String status) {
        try {
            List<Issue> issues = tools.findByStatus(status);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "status", status));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-type", description = "Récupère les issues par type. Types: BUG, REGRESSION, VULNERABILITY, INCIDENT, PERFORMANCE, FEATURE_REQUEST, IMPROVEMENT, QUESTION, SUPPORT.")
    public String findIssuesByType(String type) {
        try {
            List<Issue> issues = tools.findByType(type);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "type", type));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-severity", description = "Récupère les issues par sévérité. Sévérités: CRITICAL, HIGH, MEDIUM, LOW, INFO.")
    public String findIssuesBySeverity(String severity) {
        try {
            List<Issue> issues = tools.findBySeverity(severity);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "severity", severity));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-reporter", description = "Récupère les issues signalées par une personne (email).")
    public String findIssuesByReporter(String email) {
        try {
            List<Issue> issues = tools.findByReporter(email);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "reporter", email));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-assignee", description = "Récupère les issues assignées à une personne (email).")
    public String findIssuesByAssignee(String email) {
        try {
            List<Issue> issues = tools.findByAssignee(email);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "assignee", email));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-version", description = "Récupère les issues affectant une version spécifique.")
    public String findIssuesByVersion(String version) {
        try {
            List<Issue> issues = tools.findByAffectedVersion(version);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "affectedVersion", version));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-environment", description = "Récupère les issues constatées dans un environnement donné.")
    public String findIssuesByEnvironment(String environment) {
        try {
            List<Issue> issues = tools.findByEnvironment(environment);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "environment", environment));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-platform", description = "Récupère les issues affectant une plateforme donnée (WEB, MOBILE_IOS, API, MICROSERVICE, CLOUD_FUNCTION…).")
    public String findIssuesByPlatform(String platform) {
        try {
            List<Issue> issues = tools.findByPlatform(platform);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "platform", platform));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-issues-by-component", description = "Récupère les issues affectant un composant ou microservice donné.")
    public String findIssuesByComponent(String component) {
        try {
            List<Issue> issues = tools.findByComponent(component);
            return gson.toJson(Map.of("issues", issues, "count", issues.size(), "component", component));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-open-issues", description = "Récupère toutes les issues ouvertes (statuts: OPEN, TRIAGED, REOPENED).")
    public String findOpenIssues() {
        try {
            List<Issue> issues = tools.findOpenIssues();
            return gson.toJson(Map.of("issues", issues, "count", issues.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-critical-issues", description = "Récupère toutes les issues de sévérité CRITICAL non clôturées.")
    public String findCriticalIssues() {
        try {
            List<Issue> issues = tools.findCriticalIssues();
            return gson.toJson(Map.of("issues", issues, "count", issues.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-overdue-issues", description = "Récupère les issues dont la deadline est dépassée et non clôturées.")
    public String findOverdueIssues() {
        try {
            List<Issue> issues = tools.findOverdueIssues();
            return gson.toJson(Map.of("issues", issues, "count", issues.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "list-issue-statuses", description = "Liste tous les statuts d'issue disponibles.")
    public String listIssueStatuses() {
        try {
            return gson.toJson(Map.of("statuses", tools.listStatuses()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "list-issue-types", description = "Liste tous les types d'issue disponibles.")
    public String listIssueTypes() {
        try {
            return gson.toJson(Map.of("types", tools.listTypes()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "list-issue-severities", description = "Liste toutes les sévérités d'issue disponibles.")
    public String listIssueSeverities() {
        try {
            return gson.toJson(Map.of("severities", tools.listSeverities()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
