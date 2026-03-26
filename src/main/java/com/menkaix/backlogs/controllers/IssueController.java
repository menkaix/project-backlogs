package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.dto.IssueContextDTO;
import com.menkaix.backlogs.models.entities.Issue;
import com.menkaix.backlogs.models.values.IssueSeverity;
import com.menkaix.backlogs.models.values.IssueStatus;
import com.menkaix.backlogs.models.values.IssueType;
import com.menkaix.backlogs.services.IssueContextService;
import com.menkaix.backlogs.services.IssueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/issue")
public class IssueController {

    private final IssueService issueService;
    private final IssueContextService issueContextService;

    public IssueController(IssueService issueService, IssueContextService issueContextService) {
        this.issueService = issueService;
        this.issueContextService = issueContextService;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Issue> create(@RequestBody Issue issue) {
        return ResponseEntity.ok(issueService.create(issue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issue> findById(@PathVariable String id) {
        return issueService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Issue> update(@PathVariable String id, @RequestBody Issue issue) {
        issue.setId(id);
        return ResponseEntity.ok(issueService.update(issue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Issue>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter) {
        return ResponseEntity.ok(issueService.findAll(PageRequest.of(page, size), search, filter));
    }

    // ── Contexte ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/context")
    public ResponseEntity<?> getContext(@PathVariable String id) {
        try {
            IssueContextDTO ctx = issueContextService.buildContext(id);
            return ResponseEntity.ok(ctx);
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Statut ────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body("Le champ 'status' est obligatoire");
        }
        try {
            return ResponseEntity.ok(issueService.updateStatus(id, status));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/statuses")
    public ResponseEntity<IssueStatus[]> listStatuses() {
        return ResponseEntity.ok(issueService.listStatuses());
    }

    @GetMapping("/types")
    public ResponseEntity<IssueType[]> listTypes() {
        return ResponseEntity.ok(issueService.listTypes());
    }

    @GetMapping("/severities")
    public ResponseEntity<IssueSeverity[]> listSeverities() {
        return ResponseEntity.ok(issueService.listSeverities());
    }

    // ── Assignation ───────────────────────────────────────────────────────────

    @PostMapping("/{id}/assignees/{email}")
    public ResponseEntity<Issue> assignPerson(@PathVariable String id, @PathVariable String email) {
        return ResponseEntity.ok(issueService.assignPerson(id, email));
    }

    @DeleteMapping("/{id}/assignees/{email}")
    public ResponseEntity<Issue> unassignPerson(@PathVariable String id, @PathVariable String email) {
        return ResponseEntity.ok(issueService.unassignPerson(id, email));
    }

    // ── Affectation à un projet ou une feature ────────────────────────────────

    @PutMapping("/{id}/project/{projectId}")
    public ResponseEntity<?> assignToProject(@PathVariable String id, @PathVariable String projectId) {
        try {
            return ResponseEntity.ok(issueService.assignToProject(id, projectId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/feature/{featureId}")
    public ResponseEntity<?> assignToFeature(@PathVariable String id, @PathVariable String featureId) {
        try {
            return ResponseEntity.ok(issueService.assignToFeature(id, featureId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Recherche ─────────────────────────────────────────────────────────────

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<Issue>> findByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(issueService.findByProjectId(projectId));
    }

    @GetMapping("/by-project-ref/{projectRef}")
    public ResponseEntity<List<Issue>> findByProjectRef(@PathVariable String projectRef) {
        return ResponseEntity.ok(issueService.findByProjectRef(projectRef));
    }

    @GetMapping("/by-feature/{featureId}")
    public ResponseEntity<List<Issue>> findByFeature(@PathVariable String featureId) {
        return ResponseEntity.ok(issueService.findByFeatureId(featureId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Issue>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(issueService.findByStatus(status));
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<Issue>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(issueService.findByType(type));
    }

    @GetMapping("/by-severity/{severity}")
    public ResponseEntity<List<Issue>> findBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(issueService.findBySeverity(severity));
    }

    @GetMapping("/by-reporter/{email}")
    public ResponseEntity<List<Issue>> findByReporter(@PathVariable String email) {
        return ResponseEntity.ok(issueService.findByReporter(email));
    }

    @GetMapping("/by-assignee/{email}")
    public ResponseEntity<List<Issue>> findByAssignee(@PathVariable String email) {
        return ResponseEntity.ok(issueService.findByAssigneeEmail(email));
    }

    @GetMapping("/by-version/{version}")
    public ResponseEntity<List<Issue>> findByAffectedVersion(@PathVariable String version) {
        return ResponseEntity.ok(issueService.findByAffectedVersion(version));
    }

    @GetMapping("/by-environment/{environment}")
    public ResponseEntity<List<Issue>> findByEnvironment(@PathVariable String environment) {
        return ResponseEntity.ok(issueService.findByEnvironment(environment));
    }

    @GetMapping("/by-platform/{platform}")
    public ResponseEntity<List<Issue>> findByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(issueService.findByPlatform(platform));
    }

    @GetMapping("/by-component/{component}")
    public ResponseEntity<List<Issue>> findByComponent(@PathVariable String component) {
        return ResponseEntity.ok(issueService.findByComponent(component));
    }

    @GetMapping("/by-tracking-ref/{ref}")
    public ResponseEntity<Issue> findByTrackingRef(@PathVariable String ref) {
        return issueService.findByTrackingReference(ref)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/open")
    public ResponseEntity<List<Issue>> findOpen() {
        return ResponseEntity.ok(issueService.findOpenIssues());
    }

    @GetMapping("/critical")
    public ResponseEntity<List<Issue>> findCritical() {
        return ResponseEntity.ok(issueService.findCriticalIssues());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Issue>> findOverdue() {
        return ResponseEntity.ok(issueService.findOverdueIssues());
    }
}
