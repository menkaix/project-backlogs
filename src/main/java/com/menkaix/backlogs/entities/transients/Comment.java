package com.menkaix.backlogs.entities.transients;


import java.util.Date;

public class Comment {
// Suggested code may be subject to a license. Learn more: ~LicenseLog:2994597420.
    private String author;
    private String text;
    private Date createDate = new Date();

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {        
        this.author = author;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
    }

}
