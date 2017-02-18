package com.ggccnu.tinynote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishaowei on 16/1/7.
 */
public class NoteDbInstance {
    private static final String DB_NAME = "NoteStore.db";
    // modifed on 17/2/12
    private static final int VERSION = 2;
    private static NoteDbInstance mNoteDbInstance;
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * 将构造方法私有化
     */
    private NoteDbInstance(Context context) {
        NoteSQLiteOpenHelper dbHelper = new NoteSQLiteOpenHelper(context,
                DB_NAME, null, VERSION);
        mSQLiteDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * 获取NoteDB的实例。
     */
    public synchronized static NoteDbInstance getInstance(Context context) {
        if (mNoteDbInstance == null) {
            mNoteDbInstance = new NoteDbInstance(context);
        }
        return mNoteDbInstance;
    }

    /**
     * 从数据库读取笔记的所有年份
     */
    public List<String> QueryYears() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT DISTINCT year FROM Note ORDER BY " +
                "year ASC", null);
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String year = cursor.getString(cursor.getColumnIndex("year"));
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
        //mSQLiteDatabase.rawQuery("select id,year,month from Note where id = 1", null);
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT DISTINCT month FROM Note WHERE year = ? ORDER BY " +
                "month ASC", new String[] { year });
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String month = cursor.getString(cursor.getColumnIndex("month"));
                LogUtils.d("QueryMonths", month);
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
        Cursor cursor = mSQLiteDatabase.query("Note", new String[]{"title"}, "month=? and year=?", new String[]{month, year},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                //遍历cursor对象，取出数据
                String title = cursor.getString(cursor.getColumnIndex("title"));
                LogUtils.d("QueryTitles", title);
                list.add(title);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库中读取笔记所有信息
     */
    public Note QueryNoteDetails(String year, String month, String title) {
        Note note = new Note();
        // 查询数据库得到日记其他信息
        Cursor cursor = mSQLiteDatabase.query("Note", new String[] {"content","location", "date", "hasModified", "cmpId"},
                "title=? and month=? and year=?", new String[] {title, month, year}, null, null, null);
        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            note.setYear(year);
            note.setMonth(month);
            note.setTitle(title);
            note.setContent(content);
            note.setLocation(location);
            note.setDate(date);
            int hasModified = cursor.getInt(cursor.getColumnIndex("hasModified"));
            note.setHasModified(hasModified);
            note.setCmpId(cursor.getInt(cursor.getColumnIndex("cmpId")));
        }
        return note;
    }


    public List<Note> QueryAllNote() {
        List<Note> noteList = new ArrayList<>();

        try (Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM Note", null)) {
            if (cursor.moveToFirst()) {
                do {
                    //遍历cursor对象，取出数据
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String location = cursor.getString(cursor.getColumnIndex("location"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    Note note = new Note();
                    note.setYear(cursor.getString(cursor.getColumnIndex("year")));
                    note.setMonth(cursor.getString(cursor.getColumnIndex("month")));
                    note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    note.setContent(content);
                    note.setLocation(location);
                    note.setDate(date);
                    int hasModified = cursor.getInt(cursor.getColumnIndex("hasModified"));
                    note.setHasModified(hasModified);
                    note.setCmpId(cursor.getInt(cursor.getColumnIndex("cmpId")));

                    noteList.add(note);

                } while (cursor.moveToNext());
            }
        }
        return noteList;
    }

    public void addCmpId2NoteDb() {
        try (Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM Note", null)) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    ContentValues values = new ContentValues();
                    values.put("cmpId", id);
                    mSQLiteDatabase.update("Note", values, "id = " + id, null);
                } while (cursor.moveToNext());
            }
        }
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
        values.put("location", note.getLocation());
        values.put("date", note.getDate());
        //values.put("cmpId", note.getCmpId());
        mSQLiteDatabase.insert("Note", null, values);
    }

    public void AddCmpId2NewestNote() {
        try (Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM Note ORDER BY id DESC", null)) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                ContentValues values = new ContentValues();
                values.put("cmpId", id);
                mSQLiteDatabase.update("Note", values, "id = " + id, null);
            }
        }
    }

    public int getCmpIdOfNewestNote() {
        try (Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM Note ORDER BY id DESC", null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return 0;
    }

    /**
     * 修改笔记
     */
    public void UpdateNote(Note oldNote, Note newNote) {
        ContentValues values = new ContentValues();
        values.put("title", newNote.getTitle());
        values.put("content", newNote.getContent());
        values.put("location", newNote.getLocation());
        values.put("hasModified", newNote.getHasModified());
        mSQLiteDatabase.update("Note", values, "cmpId = " + oldNote.getCmpId(), null);
    }

    /**
     * 修改笔记是否被修改列
     */
    public void UpdateNoteIsModified(Note newNote) {
        ContentValues values = new ContentValues();
//        values.put("title", newNote.getTitle());
//        values.put("content", newNote.getContent());
//        values.put("location", newNote.getLocation());
        values.put("hasModified", newNote.getHasModified());
        mSQLiteDatabase.update("Note", values, "cmpId = " + newNote.getCmpId(), null);
    }
    /**
     * 删除笔记
     */
    public void DeleteNote(Note note) {
        mSQLiteDatabase.delete("Note","year = ? and month = ? and title = ? and content = ?",
                new String[]{note.getYear(), note.getMonth(), note.getTitle(), note.getContent()});
    }
}
