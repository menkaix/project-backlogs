package com.menkaix.backlogs.models.values;

/**
 * Cycle de vie d'une issue.
 * <pre>
 *  OPEN → TRIAGED → IN_PROGRESS → IN_REVIEW → RESOLVED → CLOSED
 *                                           ↘ WONT_FIX
 *                                           ↘ DUPLICATE
 *               ↔ NEED_MORE_INFO
 *  CLOSED → REOPENED → IN_PROGRESS ...
 * </pre>
 */
public enum IssueStatus {

    /** Créée, pas encore triée. */
    OPEN,

    /** Analysée, acceptée, priorité définie. */
    TRIAGED,

    /** En cours de résolution. */
    IN_PROGRESS,

    /** Correction en revue / en test. */
    IN_REVIEW,

    /** Correction déployée, en attente de confirmation. */
    RESOLVED,

    /** Confirmée comme résolue, clôturée. */
    CLOSED,

    /** Reconnue mais ne sera pas corrigée. */
    WONT_FIX,

    /** Doublon d'une autre issue existante. */
    DUPLICATE,

    /** En attente d'informations complémentaires du rapporteur. */
    NEED_MORE_INFO,

    /** Rouverte après une clôture prématurée. */
    REOPENED;

    public static IssueStatus normalize(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return switch (raw.trim().toLowerCase()) {
            case "open"                                   -> OPEN;
            case "triaged", "triage"                      -> TRIAGED;
            case "in_progress", "in-progress", "inprogress" -> IN_PROGRESS;
            case "in_review", "in-review", "inreview"     -> IN_REVIEW;
            case "resolved"                               -> RESOLVED;
            case "closed", "close"                        -> CLOSED;
            case "wont_fix", "wont-fix", "wontfix"        -> WONT_FIX;
            case "duplicate"                              -> DUPLICATE;
            case "need_more_info", "need-more-info", "needmoreinfo", "need_info" -> NEED_MORE_INFO;
            case "reopened", "reopen"                     -> REOPENED;
            default -> null;
        };
    }

    public boolean isTerminal() {
        return this == CLOSED || this == WONT_FIX || this == DUPLICATE;
    }

    public boolean isActive() {
        return this == IN_PROGRESS || this == IN_REVIEW || this == TRIAGED || this == REOPENED;
    }
}
