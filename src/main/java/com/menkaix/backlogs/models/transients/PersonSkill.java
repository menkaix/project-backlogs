package com.menkaix.backlogs.models.transients;

import com.menkaix.backlogs.models.values.SkillLevel;

/**
 * Objet embarqué dans People représentant la maîtrise d'un skill par une personne.
 * skillName est dénormalisé pour éviter des jointures inutiles.
 */
public class PersonSkill {

    private String skillId;
    private String skillName;
    private SkillLevel level;

    public PersonSkill() {
    }

    public PersonSkill(String skillId, String skillName, SkillLevel level) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.level = level;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }
}
