package com.mycompany.tinynote;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lishaowei on 15/10/4.
 */
public class WriteNoteActivity extends Activity {

    private MyDatabaseHelper dbHelper;

    private Button buttonWriteDone;
    // 笔记title
    private EditText noteTitle;
    // note content
    private EditText noteContent;
    // note year/month/location got from system


    private String title;
    private String content;
    private String year,month,day,location;

    //int mYear = c.get(Calendar.YEAR);
    //int mMonth = c.get(Calendar.MONTH);
    //int mDay = c.get(Calendar.DAY_OF_MONTH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        dbHelper = new MyDatabaseHelper(this, "NoteStore.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        buttonWriteDone = (Button) findViewById(R.id.write_done);
        buttonWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get note title/content/...
                noteTitle = (EditText) findViewById(R.id.note_title);
                noteContent = (EditText) findViewById(R.id.note_content);
                title = noteTitle.getText().toString();
                content = noteContent.getText().toString();

                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);

                //year = c.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.CHINA);
                year = "二零役五年";
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                //day = c.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.CHINA);
                day = "三日";
                Log.d("WriteNoteActivity","current date is year: " + year +
                "month: " + month + "day: " + day);
                // 保存日记到数据库
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                values.put("year", year);
                values.put("month", month);
                values.put("location", "wuhan");
                db.insert("Note", null, values);
                values.clear();
                // 回到主活动
                Intent intent = new Intent(WriteNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
