package com.mycompany.tinynote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mycompany.tinynote.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishaowei on 16/1/7.
 */
public class NoteDb {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "NoteStore.db";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static NoteDb noteDb;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private NoteDb(Context context) {
        NoteOpenHelper dbHelper = new NoteOpenHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取NoteDB的实例。
     */
    public synchronized static NoteDb getInstance(Context context) {
        if (noteDb == null) {
            noteDb = new NoteDb(context);
        }
        return noteDb;
    }

    /**
     * 从数据库读取笔记的所有年份
     */
    public List<String> QueryYears() {
        List<String> list = new ArrayList<String>();
        //查询笔记的年份
        //db.rawQuery("select id,year,month from Note where id = 1", null);
        Cursor cursor = db.rawQuery("SELECT DISTINCT year FROM Note ORDER BY " +
                "year ASC", null);
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String year = cursor.getString(cursor.getColumnIndex("year"));
//                Log.d("QueryYears",year);
                list.add(year);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库读取笔记的月份
     */
    public List<String> QueryMonths(String year) {
        List<String> list = new ArrayList<String>();
        // 查询给定year的所有笔记的月份
        //db.rawQuery("select id,year,month from Note where id = 1", null);
        Cursor cursor = db.rawQuery("SELECT DISTINCT month FROM Note WHERE year = ? ORDER BY " +
                "month ASC", new String[] { year });
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String month = cursor.getString(cursor.getColumnIndex("month"));
                Log.d("QueryMonths", month);
                list.add(month);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库读取笔记的title
     */
    public List<String> QueryTitles(String year, String month) {
        List<String> list = new ArrayList<String>();
        //查询NOTE表中最近月份的笔记并显示出来，
        Cursor cursor = db.query("Note", new String[]{"title"}, "month=? and year=?", new String[]{month, year},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String title = cursor.getString(cursor.getColumnIndex("title"));
                Log.d("QueryTitles", title);
                list.add(title);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库中读取笔记所有信息
     */
    public Note QueryNoteAll(String year, String month, String title) {
        Note note = new Note();
        // 查询数据库得到日记其他信息
        Cursor cursor = db.query("Note", new String[] {"content","location", "date"},
                "title=? and month=? and year=?", new String[] {title, month, year}, null, null, null);
        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            note.setYear(year);
            note.setMonth(month);
            note.setTitle(title);
            note.setContent(content);
            note.setLoacation(location);
            note.setDate(date);
        }
        return note;
    }

    /**
     * 往数据库中插入笔记
     */
    public void InsertNote(Note note) {
        // 保存日记到数据库
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("year", note.getYear());
        values.put("month", note.getMonth());
        values.put("location", note.getLoacation());
        values.put("date", note.getDate());
        db.insert("Note", null, values);
    }

    /**
     * 修改笔记
     */
    public void UpdateNote(Note oldNote, ContentValues values) {
        // 保存日记到数据库
        db.update("Note", values, "year = ? and month = ? and title = ? and content = ?",
                new String[]{oldNote.getYear(), oldNote.getMonth(), oldNote.getTitle(), oldNote.getContent()});
    }
    /**
     * 删除笔记
     */
    public void DeleteNote(Note note) {
        db.delete("Note","year = ? and month = ? and title = ? and content = ?",
                new String[]{note.getYear(), note.getMonth(), note.getTitle(), note.getContent()});
    }
}
