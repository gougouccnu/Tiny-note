package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.model.Note;

/**
 * Created by lishaowei on 15/10/7.
 */
public class ModifyNoteActivity extends Activity {

    private static final String TAG = "WriteNoteActivity";
    private NoteDbInstance mNoteDbInstance;
    // 要更新的笔记
    private Note note = new Note();

    private Button btWriteDone;
    private EditText etTitle, etContent;
    private String title, content, year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        etTitle = (EditText) findViewById(R.id.note_title);
        etContent = (EditText) findViewById(R.id.note_content);
        btWriteDone = (Button) findViewById(R.id.write_done);

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

        if (oldTitle != null && !oldTitle.equals("")) {
            etTitle.setText(oldTitle);
            etContent.setText(oldContent);
        }
        mNoteDbInstance = NoteDbInstance.getInstance(this);
        btWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                content = etContent.getText().toString();

                // 保存日记到数据库
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                mNoteDbInstance.UpdateNote(note, values);

                // 回到主活动
                Intent intent = new Intent(ModifyNoteActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", note.getYear());
                intent.putExtra("extra_noteMonth", note.getMonth());
                startActivity(intent);
            }
        });

    }
}
