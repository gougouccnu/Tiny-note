package com.ggccnu.tinynote.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by lishaowei on 2017/2/11.
 */

public class BmobNote extends BmobObject{
    private String year,month,title,content, location,date;
    private BmobUser user;

    // added in v1.4
    private int cmpId;
    private int hasModified;

    public BmobNote() {
    }

    public BmobNote(BmobUser user, String content, String date, Integer hasModified, Integer cmpId, String location, String month, String title, String year) {
        this.user = user;
        this.content = content;
        this.date = date;
        this.hasModified = hasModified;
        this.cmpId = cmpId;
        this.location = location;
        this.month = month;
        this.title = title;
        this.year = year;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHasModified(int hasModified) {
        this.hasModified = hasModified;
    }

    public void setCmpId(int id) {
        this.cmpId = id;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public int getModified() {
        return hasModified;
    }

    public int getCmpId() {
        return cmpId;
    }

    public String getLocation() {
        return location;
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
