package com.menkaix.backlogs.models.transients;

import java.util.Date;

public class Link {

    private String href;
    private String version;
    private String name;

    private Date createDate = new Date();

// Suggested code may be subject to a license. Learn more: ~LicenseLog:1339827003.
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

// Suggested code may be subject to a license. Learn more: ~LicenseLog:1680790035.
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

// Suggested code may be subject to a license. Learn more: ~LicenseLog:1423992242.
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
