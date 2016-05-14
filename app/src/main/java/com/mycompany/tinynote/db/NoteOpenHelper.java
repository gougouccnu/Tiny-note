package com.mycompany.tinynote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lishaowei on 16/1/7.
 */
public class NoteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_NOTE = "create table Note ("
            + "id integer primary key autoincrement, "
            + "year text, "
            + "month text, "
            + "title text, "
            + "content text, "
            + "location text,"
            + "date text)";

    private Context mContext;

    public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
        db.execSQL("insert into Note (year, month, title, content, location, date) values (?,?,?,?,?,?)",
                new String[] {"二零一五年", "十二月", "远方", "内容", "武汉", "八日"});
        db.execSQL("insert into Note (year, month, title, content, location, date) values (?,?,?,?,?,?)",
                new String[] {"二零一五年", "十二月", "工作", "学习 生活的", "武汉", "十八日"});
        Log.d("MyDatabaseHelper", "mydatabase created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            case 4:
                db.execSQL("alter table Note add column date text");
                Log.d("NoteOpenHelper", "alter table Note");
            default:
        }
    }
    //TODO

}
