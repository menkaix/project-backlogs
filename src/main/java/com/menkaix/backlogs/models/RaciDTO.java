package com.menkaix.backlogs.models;

import java.util.List;

public class RaciDTO {
    private String projectCode;
    private List<String> r;
    private List<String> a;
    private List<String> c;
    private List<String> i;

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public List<String> getR() {
        return r;
    }

    public void setR(List<String> r) {
        this.r = r;
    }

    public List<String> getA() {
        return a;
    }

    public void setA(List<String> a) {
        this.a = a;
    }

    public List<String> getC() {
        return c;
    }

    public void setC(List<String> c) {
        this.c = c;
    }

    public List<String> getI() {
        return i;
    }

    public void setI(List<String> i) {
        this.i = i;
    }
}
