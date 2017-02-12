package com.ggccnu.tinynote.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lishaowei on 2017/2/11.
 */

public class BmobNote extends BmobObject{
    private String year,month,title,content,loacation,date;

    // added in v1.4
    private Integer noteId;
    private Integer hasUpload;

    public BmobNote() {
    }

    public BmobNote(String content, String date, Integer hasUpload, Integer noteId, String loacation, String month, String title, String year) {
        this.content = content;
        this.date = date;
        this.hasUpload = hasUpload;
        this.noteId = noteId;
        this.loacation = loacation;
        this.month = month;
        this.title = title;
        this.year = year;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHasUpload(Integer hasUpload) {
        this.hasUpload = hasUpload;
    }

    public void setNoteId(Integer id) {
        this.noteId = id;
    }

    public void setLoacation(String loacation) {
        this.loacation = loacation;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public Integer getHasUpload() {
        return hasUpload;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public String getLoacation() {
        return loacation;
    }

    public String getMonth() {
        return month;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }
}
