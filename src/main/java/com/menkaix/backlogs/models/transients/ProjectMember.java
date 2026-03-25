package com.menkaix.backlogs.models.transients;

import java.util.ArrayList;
import java.util.List;

/**
 * Membre d'une équipe projet, embarqué dans Project.
 * Les champs identitaires et le skillset sont dénormalisés depuis People
 * pour éviter les jointures lors de l'affichage du projet.
 */
public class ProjectMember {

    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private List<PersonSkill> skills = new ArrayList<>();

    public ProjectMember() {
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PersonSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<PersonSkill> skills) {
        this.skills = skills;
    }
}
