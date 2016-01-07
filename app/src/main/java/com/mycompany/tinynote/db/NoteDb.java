package com.mycompany.tinynote.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String DB_NAME = "note";

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
    public List<String> loadYears() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = db
                .query("year", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.

                note.setId(cursor.getInt(cursor.getColumnIndex("id")));
                note.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("note_name")));
                note.setProvinceCode(cursor.getString(cursor
                        .getColumnIndex("note_code")));
                list.add(note);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
