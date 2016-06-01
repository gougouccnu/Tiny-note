package com.ggccnu.tinynote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ggccnu.tinynote.util.DateConvertor;

import java.util.Calendar;
import java.util.Locale;

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
    private String year,month,day;

    public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 初始化数据库，初始化3条当年当月的3条笔记
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        //year = c.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.CHINA);
        year = DateConvertor.formatYear(y);
        month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
        //day = c.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.CHINA);
        day = DateConvertor.formatDay(d);
        Log.d("NoteOpenHelper", "current date is year: " + year +
                "month: " + month + "day: " + day);
        db.execSQL(CREATE_NOTE);
        db.execSQL("insert into Note (year, month, title, content, location, date) values (?,?,?,?,?,?)",
                new String[] {year, month, "要有光", "上帝说\n要有光\n于是\n便有了光 ", "湖北武汉", year + month + day});
        db.execSQL("insert into Note (year, month, title, content, location, date) values (?,?,?,?,?,?)",
                new String[] {year, month, "季风气候", "没道理\n是一枚太平洋的暖湿空气\n飘散了我们的心\n在青春的墓地\n就这样平行下去", "于武汉", year + month +day});
        Log.d("MyDatabaseHelper", "mydatabase created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
//            case 4:
//                db.execSQL("alter table Note add column date text");
//                Log.d("NoteOpenHelper", "alter table Note");
            default:
        }
    }
    //TODO

}
