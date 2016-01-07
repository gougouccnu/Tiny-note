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
            + "location text)";

    private Context mContext;

    public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
        Log.d("MyDatabaseHelper", "mydatabase created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
