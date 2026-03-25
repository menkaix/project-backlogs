package com.menkaix.backlogs.models.values;

public enum SkillLevel {

    NEVER_HEARD(0, "Jamais entendu parlé"),
    HEARD_OF(1, "J'en ai juste entendu parlé"),
    THEORETICAL(2, "Je connais en théorie"),
    POC(3, "J'ai fait au moins un PoC dessus"),
    ONE_PROJECT(4, "Je l'ai utilisé dans un projet réel"),
    SEVERAL_PROJECTS(5, "Je l'ai utilisé dans plusieurs projets réels"),
    REGULAR_USE(6, "Je l'utilise régulièrement");

    private final int rank;
    private final String label;

    SkillLevel(int rank, String label) {
        this.rank = rank;
        this.label = label;
    }

    public int getRank() {
        return rank;
    }

    public String getLabel() {
        return label;
    }
}
