package com.ggccnu.tinynote.model;

/**
 * Created by lishaowei on 16/1/7.
 */
public class Note {
    private String year,month,title,content,loacation,date;

    // added in v1.4
    private Integer noteId;
    private int hasUpload;

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


    public int getHasUpload() {
        return hasUpload;
    }

    public Integer getId() {
        return noteId;
    }

    public void setHasUpload(int hasUpload) {
        this.hasUpload = hasUpload;
    }

    public void setId(Integer id) {
        this.noteId = id;
    }
}
