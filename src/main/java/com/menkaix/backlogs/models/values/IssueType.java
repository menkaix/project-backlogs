package com.menkaix.backlogs.models.values;

/**
 * Catégorie fonctionnelle d'une issue.
 */
public enum IssueType {

    /** Comportement incorrect par rapport à la spécification. */
    BUG,

    /** Fonctionnalité qui marchait et ne marche plus. */
    REGRESSION,

    /** Faille de sécurité (injection, exposition de données, OWASP, CVE…). */
    VULNERABILITY,

    /** Incident de production (service down, perte de données, SLA breach). */
    INCIDENT,

    /** Dégradation mesurable des performances (latence, throughput, mémoire…). */
    PERFORMANCE,

    /** Demande d'une nouvelle fonctionnalité par un utilisateur ou le métier. */
    FEATURE_REQUEST,

    /** Amélioration d'une fonctionnalité existante. */
    IMPROVEMENT,

    /** Question technique ou fonctionnelle sans bug identifié. */
    QUESTION,

    /** Demande de support / assistance utilisateur. */
    SUPPORT
}
