package com.menkaix.backlogs.models.transients;

import java.util.Date;

public class Link {

    private String href;
    private String version;
    private String name;

    private Date createDate = new Date();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Link(String href, String version, String name) {
        this.href = href;
        this.version = version;
        this.name = name;
    }



}
