package com.menkaix.backlogs.models.values;

public enum TaskStatus {

    NEW,
    UNKNOWN,
    TODO,
    PENDING,
    TO_SPEC,
    SPECIFYING,
    IN_PROGRESS,
    RND,
    TO_STUDY,
    TO_TEST,
    TESTING,
    DONE,
    CANCELED;

    /**
     * Normalise une valeur brute (anciens libellés MongoDB) vers l'enum correspondant.
     * Retourne {@code UNKNOWN} si aucune correspondance reconnue n'est trouvée.
     * Retourne {@code null} uniquement si {@code raw} est null ou vide.
     */
    public static TaskStatus normalize(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return switch (raw.trim().toLowerCase()) {
            case "new"                                       -> NEW;
            case "unknown"                                   -> UNKNOWN;
            case "todo", "to-do", "to_do"                   -> TODO;
            case "pending"                                   -> PENDING;
            case "to-spec", "to_spec", "tospec"             -> TO_SPEC;
            case "specifying"                                -> SPECIFYING;
            case "in-progress", "in_progress", "inprogress" -> IN_PROGRESS;
            case "rnd", "r&d"                                -> RND;
            case "to-study", "to_study", "tostudy"          -> TO_STUDY;
            case "to-test", "to_test", "totest"              -> TO_TEST;
            case "testing"                                   -> TESTING;
            case "done"                                      -> DONE;
            case "canceled", "cancelled"                     -> CANCELED;
            default -> UNKNOWN;
        };
    }

    /**
     * Indique si ce statut correspond à un état "terminal" (tâche finie ou annulée).
     */
    public boolean isTerminal() {
        return this == DONE || this == CANCELED;
    }

    /**
     * Indique si ce statut correspond à un état "actif" (travail en cours ou à faire).
     */
    public boolean isActive() {
        return this == NEW || this == TODO || this == IN_PROGRESS
                || this == TO_SPEC || this == SPECIFYING
                || this == RND || this == TO_STUDY
                || this == TO_TEST || this == TESTING;
    }
}
