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
 * Created by lishaowei on 15/10/7.
 */
public class ModifyNoteActivity extends Activity {

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

        Intent intent = getIntent();
        final String oldTitle = intent.getStringExtra("extra_modify_title");
        final String oldContent = intent.getStringExtra("extra_modify_content");
        if (oldTitle == null || oldTitle.equals("")) {
            // DO Nothing
        } else { // 日记修改活动
            noteTitle = (EditText) findViewById(R.id.note_title);
            noteContent = (EditText) findViewById(R.id.note_content);
            noteTitle.setText(oldTitle);
            noteContent.setText(oldContent);
        }

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

                year = "二零而五年";
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                day = "七日";
                Log.d("WriteNoteActivity", "current date is year: " + year +
                        "month: " + month + "day: " + day);
                // 保存日记到数据库
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                //values.put("year", year);
                //values.put("month", month);
                //values.put("location", "wuhan");
                db.update("Note", values, "title = ? and content = ?",
                        new String[]{oldTitle,oldContent});
                values.clear();
                // 回到主活动
                Intent intent = new Intent(ModifyNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
