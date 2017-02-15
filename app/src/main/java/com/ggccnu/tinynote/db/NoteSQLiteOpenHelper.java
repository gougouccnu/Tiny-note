package com.ggccnu.tinynote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.util.DateConvertor;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lishaowei on 16/1/7.
 */
public class NoteSQLiteOpenHelper extends SQLiteOpenHelper {

    private final String TAG = "NoteSQLiteOpenHelper";

    private NoteDbInstance mNoteDbInstance;

    public static final String CREATE_NOTE = "create table Note ("
            + "id integer primary key autoincrement, "
            + "year text, "
            + "month text, "
            + "title text, "
            + "content text, "
            + "location text,"
            + "hasUpload integer,"
            + "cmpId integer,"
            + "date text)";

    public NoteSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 初始化数据库，初始化3条当年当月的3条笔记
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        //year = c.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.CHINA);
        String year = DateConvertor.formatYear(y);
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
        //day = c.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.CHINA);
        String day = DateConvertor.formatDay(d);
        LogUtils.d("NoteSQLiteOpenHelper", "current date is year: " + year +
                "month: " + month + "day: " + day);
        db.execSQL(CREATE_NOTE);
        db.execSQL("insert into Note (year, month, title, content, location, date, hasUpload, cmpId) values (?,?,?,?,?,?,0,1)",
                new String[] {year, month, "要有光", "上帝说\n要有光\n于是\n便有了光 ", "湖北武汉", year + month + day});
        db.execSQL("insert into Note (year, month, title, content, location, date, hasUpload, cmpId) values (?,?,?,?,?,?,0,2)",
                new String[] {year, month, "季风气候", "没道理\n是一枚太平洋的暖湿空气\n飘散了我们的心\n在青春的墓地\n就这样平行下去", "于武汉", year + month + day});
        LogUtils.d(TAG, "mydatabase created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table Note add column hasUpload integer");
                db.execSQL("alter table Note add column cmpId integer");

                try (Cursor cursor = db.rawQuery("SELECT * FROM Note", null)) {
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                            ContentValues values = new ContentValues();
                            values.put("cmpId", id);
                            db.update("Note", values, "id = " + id, null);
                        } while (cursor.moveToNext());
                    }
                }

                LogUtils.d("NoteSQLiteOpenHelper", "alter table Note");
            default:
        }
    }
    //TODO

}
