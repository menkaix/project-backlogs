package com.menkaix.backlogs.models.values;

/**
 * Sévérité d'une issue — mesure l'impact technique et métier.
 * Distincte de la priorité (décision de traitement).
 */
public enum IssueSeverity {

    /**
     * Service indisponible, perte de données, brèche de sécurité active,
     * SLA critique non respecté. Intervention immédiate requise 24/7.
     */
    CRITICAL,

    /**
     * Fonctionnalité majeure inutilisable, aucun contournement disponible,
     * impact fort sur un grand nombre d'utilisateurs.
     */
    HIGH,

    /**
     * Fonctionnalité partiellement dégradée, contournement possible.
     * Impact modéré ou limité à certains contextes/plateformes.
     */
    MEDIUM,

    /**
     * Problème mineur, cosmétique ou edge case rare.
     * Aucun impact fonctionnel significatif.
     */
    LOW,

    /**
     * Observation, suggestion ou information sans impact opérationnel.
     */
    INFO
}
