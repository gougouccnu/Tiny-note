package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.db.NoteDb;
import com.ggccnu.tinynote.model.Note;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lishaowei on 15/10/7.
 */
public class ModifyNoteActivity extends Activity {

    private static final String TAG = "WriteNoteActivity";
    private NoteDb noteDb;
    // 要更新的笔记
    private Note note = new Note();

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
        final String oldYear = intent.getStringExtra("extra_modify_year");
        final String oldMonth = intent.getStringExtra("extra_modify_month");
        final String oldTitle = intent.getStringExtra("extra_modify_title");
        final String oldContent = intent.getStringExtra("extra_modify_content");
        final String oldLocation = intent.getStringExtra("extra_modify_location");
        final String oldDate = intent.getStringExtra("extra_modify_date");
        note.setYear(oldYear);
        note.setMonth(oldMonth);
        note.setTitle(oldTitle);
        note.setContent(oldContent);
        note.setLoacation(oldLocation);
        note.setDate(oldDate);
        if (oldTitle == null || oldTitle.equals("")) {
            // DO Nothing
        } else { // 日记修改活动
            noteTitle = (EditText) findViewById(R.id.note_title);
            noteContent = (EditText) findViewById(R.id.note_content);
            noteTitle.setText(oldTitle);
            noteContent.setText(oldContent);
        }
        noteDb = NoteDb.getInstance(this);
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

                year = "二零一五年";
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                day = "七日";
                // Log.d("WriteNoteActivity", "current date is year: " + year +
                //        "month: " + month + "day: " + day);
                LogUtils.d(TAG, "current date is year: " + year +
                        "month: " + month + "day: " + day);
                // 保存日记到数据库
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                noteDb.UpdateNote(note, values);
                // 回到主活动
                Intent intent = new Intent(ModifyNoteActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", note.getYear());
                intent.putExtra("extra_noteMonth", note.getMonth());
                startActivity(intent);
            }
        });

    }
}
