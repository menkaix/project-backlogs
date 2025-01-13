package com.menkaix.backlogs.models.entities;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

public class Raci {

    @Id
    public String id;

    private String projectID;

    private List<String> responsible;
    private List<String> accountable;
    private List<String> consulted;
    private List<String> informed;

    public String getprojectID() {
        return this.projectID;
    }

    public void setprojectID(String projectID) {
        this.projectID = projectID;
    }

    public List<String> getResponsible() {
        return this.responsible;
    }

    public void setResponsible(List<String> responsible) {
        this.responsible = responsible;
    }

    public List<String> getAccountable() {
        return this.accountable;
    }

    public void setAccountable(List<String> accountable) {
        this.accountable = accountable;
    }

    public List<String> getConsulted() {
        return this.consulted;
    }

    public void setConsulted(List<String> consulted) {
        this.consulted = consulted;
    }

    public List<String> getInformed() {
        return this.informed;
    }

    public void setInformed(List<String> informed) {
        this.informed = informed;
    }

    public Raci() {
        responsible = new ArrayList<String>();
        accountable = new ArrayList<String>();
        consulted = new ArrayList<String>();
        informed = new ArrayList<String>();
    }

}
