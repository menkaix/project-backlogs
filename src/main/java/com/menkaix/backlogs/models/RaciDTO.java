package com.menkaix.backlogs.models;

import java.util.List;

public class RaciDTO {

    private String ProjectCode ;

    private List<String> R ;
    private List<String> A ;
    private List<String> C ;
    private List<String> I ;

    public String getProjectCode() {
        return this.ProjectCode;
    }
    
    public void setProjectCode(String ProjectCode) {
        this.ProjectCode = ProjectCode;
    }


    public List<String> getR() {
        return this.R;
    }
    
    public void setR(List<String> R) {
        this.R = R;
    }

    public List<String> getA() {
        return this.A;
    }
    
    public void setA(List<String> A) {
        this.A = A;
    }
    
    public List<String> getC() {
        return this.C;
    }
    
    public void setC(List<String> C) {
        this.C = C;
    }
    
    public List<String> getI() {
        return this.I;
    }
    
    public void setI(List<String> I) {
        this.I = I;
    }
    


}
