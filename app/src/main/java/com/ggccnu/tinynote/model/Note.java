package com.ggccnu.tinynote.model;

/**
 * Created by lishaowei on 16/1/7.
 */
public class Note {
    private String year,month,title,content,loacation,date;

    // added in v1.4
    private int cmpId;
    private int hasModified;

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getLoacation() {
        return loacation;
    }

    public String getDate() {
        return date;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLoacation(String loacation) {
        this.loacation = loacation;
    }

    public void setDate(String date) {
        this.date = date;
    }
    //TODO


    public int getHasModified() {
        return hasModified;
    }

    public Integer getCmpId() {
        return cmpId;
    }

    public void setHasModified(int hasModified) {
        this.hasModified = hasModified;
    }

    public void setCmpId(Integer id) {
        this.cmpId = id;
    }
}
