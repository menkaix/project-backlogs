package com.menkaix.backlogs.models.entities;

import com.menkaix.backlogs.models.values.IssueSeverity;
import com.menkaix.backlogs.models.values.IssueStatus;
import com.menkaix.backlogs.models.values.IssueType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Issue — bug, retour utilisateur, incident ou demande d'amélioration.
 *
 * Étend {@link Task} pour partager le modèle commun (id, projectId, idReference,
 * assignees, dates, estimation…) et apparaître dans les listes de tâches.
 *
 * Peut être attachée :
 *   - directement à un projet via {@link #projectId}
 *   - à une feature via {@link #idReference} = "feature/{featureId}"
 *
 * Champs spécifiques au bug-tracking multiplateforme / cloud / microservices :
 *   - type, severity : classification et impact
 *   - reporter : qui a signalé
 *   - environment, platform, component : où ça se passe
 *   - affectedVersion / fixedInVersion : traçabilité par release
 *   - reproductionSteps, expectedBehavior, actualBehavior : reproductibilité
 *   - workaround : contournement temporaire
 *   - duplicateOfId : lien vers l'issue parente si doublon
 *   - externalReference : ticket Jira, GitHub issue, PagerDuty alert…
 *   - resolution : description de la correction appliquée
 *   - labels, attachments : enrichissement libre
 */
@Document(collection = "issues")
public class Issue extends Task {

    // ── Classification ────────────────────────────────────────────────────────

    private IssueType type = IssueType.BUG;
    private IssueSeverity severity = IssueSeverity.MEDIUM;

    /**
     * Priorité de traitement (libre : URGENT, HIGH, NORMAL, LOW).
     * Distincte de la sévérité (impact) — décision de l'équipe.
     */
    private String priority;

    /** Email de la personne qui a signalé l'issue. */
    private String reporter;

    // ── Contexte technique (multiplateforme / cloud / microservices) ──────────

    /**
     * Environnement où l'issue a été constatée.
     * Peut référencer le nom d'un {@link com.menkaix.backlogs.models.transients.ProjectEnvironment}.
     */
    private String environment;

    /**
     * Plateforme concernée.
     * Exemples : WEB, MOBILE_IOS, MOBILE_ANDROID, API, MICROSERVICE,
     *            CLOUD_FUNCTION, SERVERLESS, BACKEND, FRONTEND, DATABASE,
     *            INFRASTRUCTURE, MONITORING.
     */
    private String platform;

    /**
     * Composant ou microservice concerné.
     * Exemples : auth-service, payment-api, notification-worker, CDN…
     */
    private String component;

    // ── Versioning ────────────────────────────────────────────────────────────

    /** Version(s) où le problème a été constaté. */
    private String affectedVersion;

    /** Version dans laquelle la correction a été déployée. */
    private String fixedInVersion;

    // ── Reproduction ─────────────────────────────────────────────────────────

    /** Étapes pour reproduire le problème. */
    private String reproductionSteps;

    /** Comportement attendu selon la spécification. */
    private String expectedBehavior;

    /** Comportement observé / problème constaté. */
    private String actualBehavior;

    /** Contournement temporaire disponible en attendant le correctif. */
    private String workaround;

    // ── Liens et métadonnées ──────────────────────────────────────────────────

    /** Labels / étiquettes libres (ex : "regression", "hotfix", "perf"). */
    private List<String> labels = new ArrayList<>();

    /** URLs vers captures d'écran, logs, traces, vidéos… */
    private List<String> attachments = new ArrayList<>();

    /**
     * Référence externe : numéro Jira, GitHub Issue, PagerDuty alert,
     * Sentry event ID, Datadog monitor…
     */
    private String externalReference;

    /**
     * ID de l'issue dont celle-ci est un doublon.
     * Renseigné quand {@link #status} = DUPLICATE.
     */
    private String duplicateOfId;

    /** Description de la correction ou de la décision de fermeture. */
    private String resolution;

    // ── Date de clôture ───────────────────────────────────────────────────────

    private Date closedDate;

    // ── Constructeur ─────────────────────────────────────────────────────────

    public Issue() {
        this.entityType = "ISSUE";
        this.status = IssueStatus.OPEN.name();
    }

    // ── Surcharge du statut (utilise IssueStatus, pas TaskStatus) ────────────

    @Override
    public void setStatus(String status) {
        IssueStatus normalized = IssueStatus.normalize(status);
        this.status = (normalized != null) ? normalized.name() : IssueStatus.OPEN.name();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public IssueType getType() { return type; }
    public void setType(IssueType type) { this.type = type; }

    public IssueSeverity getSeverity() { return severity; }
    public void setSeverity(IssueSeverity severity) { this.severity = severity; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getReporter() { return reporter; }
    public void setReporter(String reporter) { this.reporter = reporter; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getAffectedVersion() { return affectedVersion; }
    public void setAffectedVersion(String affectedVersion) { this.affectedVersion = affectedVersion; }

    public String getFixedInVersion() { return fixedInVersion; }
    public void setFixedInVersion(String fixedInVersion) { this.fixedInVersion = fixedInVersion; }

    public String getReproductionSteps() { return reproductionSteps; }
    public void setReproductionSteps(String reproductionSteps) { this.reproductionSteps = reproductionSteps; }

    public String getExpectedBehavior() { return expectedBehavior; }
    public void setExpectedBehavior(String expectedBehavior) { this.expectedBehavior = expectedBehavior; }

    public String getActualBehavior() { return actualBehavior; }
    public void setActualBehavior(String actualBehavior) { this.actualBehavior = actualBehavior; }

    public String getWorkaround() { return workaround; }
    public void setWorkaround(String workaround) { this.workaround = workaround; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels != null ? labels : new ArrayList<>(); }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments != null ? attachments : new ArrayList<>(); }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getDuplicateOfId() { return duplicateOfId; }
    public void setDuplicateOfId(String duplicateOfId) { this.duplicateOfId = duplicateOfId; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public Date getClosedDate() { return closedDate; }
    public void setClosedDate(Date closedDate) { this.closedDate = closedDate; }
}
