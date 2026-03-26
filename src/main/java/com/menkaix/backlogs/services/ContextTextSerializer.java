package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.dto.IssueContextDTO;
import com.menkaix.backlogs.models.dto.IssueContextDTO.IssueSnapshot;
import com.menkaix.backlogs.models.dto.TaskContextDTO;
import com.menkaix.backlogs.models.dto.TaskContextDTO.TaskSnapshot;
import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Story;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts rich context DTOs into a flat structured text suitable for embedding.
 * Each section is labelled so the model can weight semantics correctly.
 */
@Component
public class ContextTextSerializer {

    // ── Task context ─────────────────────────────────────────────────────────

    public String toText(TaskContextDTO ctx) {
        StringBuilder sb = new StringBuilder();

        TaskSnapshot t = ctx.getTask();
        if (t != null) {
            append(sb, "TASK", t.getTitle());
            append(sb, "DESCRIPTION", t.getDescription());
            append(sb, "STATUS", t.getStatus());
            append(sb, "ESTIMATE", t.getEstimate());
            if (t.getAssignees() != null && !t.getAssignees().isEmpty()) {
                append(sb, "ASSIGNEES", String.join(", ", t.getAssignees()));
            }
        }

        if (ctx.getFeature() != null) {
            append(sb, "FEATURE", ctx.getFeature().getName());
            append(sb, "FEATURE_DESCRIPTION", ctx.getFeature().getDescription());
            append(sb, "FEATURE_TYPE", ctx.getFeature().getType());
        }

        if (ctx.getStory() != null) {
            append(sb, "STORY_ACTION", ctx.getStory().getAction());
            append(sb, "STORY_OBJECTIVE", ctx.getStory().getObjective());
            append(sb, "STORY_SCENARIO", ctx.getStory().getScenario());
        }

        if (ctx.getActor() != null) {
            append(sb, "ACTOR", ctx.getActor().getName());
            append(sb, "ACTOR_TYPE", ctx.getActor().getType());
        }

        if (ctx.getProject() != null) {
            append(sb, "PROJECT", ctx.getProject().getName());
            append(sb, "PROJECT_DESCRIPTION", ctx.getProject().getDescription());
            append(sb, "CLIENT", ctx.getProject().getClientName());
        }

        if (ctx.getSiblingTasks() != null && !ctx.getSiblingTasks().isEmpty()) {
            List<String> siblings = ctx.getSiblingTasks().stream()
                    .map(TaskSnapshot::getTitle)
                    .filter(s -> s != null && !s.isBlank())
                    .toList();
            if (!siblings.isEmpty()) {
                append(sb, "SIBLING_TASKS", String.join(" | ", siblings));
            }
        }

        return sb.toString().trim();
    }

    // ── Issue context ─────────────────────────────────────────────────────────

    public String toText(IssueContextDTO ctx) {
        StringBuilder sb = new StringBuilder();

        IssueSnapshot i = ctx.getIssue();
        if (i != null) {
            append(sb, "ISSUE", i.getTitle());
            append(sb, "DESCRIPTION", i.getDescription());
            append(sb, "STATUS", i.getStatus());
            append(sb, "TYPE", i.getType());
            append(sb, "SEVERITY", i.getSeverity());
            append(sb, "PRIORITY", i.getPriority());
            append(sb, "COMPONENT", i.getComponent());
            append(sb, "PLATFORM", i.getPlatform());
            append(sb, "ENVIRONMENT", i.getEnvironment());
            append(sb, "REPRODUCTION_STEPS", i.getReproductionSteps());
            append(sb, "EXPECTED_BEHAVIOR", i.getExpectedBehavior());
            append(sb, "ACTUAL_BEHAVIOR", i.getActualBehavior());
            append(sb, "WORKAROUND", i.getWorkaround());
            append(sb, "RESOLUTION", i.getResolution());
            if (i.getLabels() != null && !i.getLabels().isEmpty()) {
                append(sb, "LABELS", String.join(", ", i.getLabels()));
            }
            if (i.getAssignees() != null && !i.getAssignees().isEmpty()) {
                append(sb, "ASSIGNEES", String.join(", ", i.getAssignees()));
            }
        }

        if (ctx.getFeature() != null) {
            append(sb, "FEATURE", ctx.getFeature().getName());
            append(sb, "FEATURE_DESCRIPTION", ctx.getFeature().getDescription());
            append(sb, "FEATURE_TYPE", ctx.getFeature().getType());
        }

        if (ctx.getStory() != null) {
            append(sb, "STORY_ACTION", ctx.getStory().getAction());
            append(sb, "STORY_OBJECTIVE", ctx.getStory().getObjective());
        }

        if (ctx.getActor() != null) {
            append(sb, "ACTOR", ctx.getActor().getName());
            append(sb, "ACTOR_TYPE", ctx.getActor().getType());
        }

        if (ctx.getProject() != null) {
            append(sb, "PROJECT", ctx.getProject().getName());
            append(sb, "PROJECT_DESCRIPTION", ctx.getProject().getDescription());
            append(sb, "CLIENT", ctx.getProject().getClientName());
        }

        if (ctx.getSiblingIssues() != null && !ctx.getSiblingIssues().isEmpty()) {
            List<String> siblings = ctx.getSiblingIssues().stream()
                    .map(IssueSnapshot::getTitle)
                    .filter(s -> s != null && !s.isBlank())
                    .toList();
            if (!siblings.isEmpty()) {
                append(sb, "SIBLING_ISSUES", String.join(" | ", siblings));
            }
        }

        return sb.toString().trim();
    }

    // ── Project tree ──────────────────────────────────────────────────────────

    public String toText(Project project, List<Feature> features, List<Story> stories) {
        StringBuilder sb = new StringBuilder();

        append(sb, "PROJECT", project.getName());
        append(sb, "PROJECT_CODE", project.getCode());
        append(sb, "DESCRIPTION", project.getDescription());
        append(sb, "CLIENT", project.getClientName());
        if (project.getPhase() != null) {
            append(sb, "PHASE", project.getPhase().name());
        }

        if (features != null) {
            for (Feature f : features) {
                append(sb, "FEATURE", f.getName());
                append(sb, "FEATURE_DESCRIPTION", f.getDescription());
                append(sb, "FEATURE_TYPE", f.getType());
            }
        }

        if (stories != null) {
            for (Story s : stories) {
                append(sb, "STORY_ACTION", s.getAction());
                append(sb, "STORY_OBJECTIVE", s.getObjective());
            }
        }

        return sb.toString().trim();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void append(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(label).append(": ").append(value).append("\n");
        }
    }
}
