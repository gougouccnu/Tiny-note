package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.model.BmobNote;
import com.ggccnu.tinynote.model.Note;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lishaowei on 15/10/7.
 */
public class ModifyNoteActivity extends Activity {

    private static final String TAG = "WriteNoteActivity";
    private NoteDbInstance mNoteDbInstance;
    // 要更新的笔记
    private Note oldNote = new Note();
    private Note newNote = new Note();

    private Button btWriteDone;
    private EditText etTitle, etContent, etLocation;
    private String title, content, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        etTitle = (EditText) findViewById(R.id.note_title);
        etContent = (EditText) findViewById(R.id.note_content);
        btWriteDone = (Button) findViewById(R.id.write_done);
        etLocation = (EditText) findViewById(R.id.note_location);

        Intent intent = getIntent();
        final String oldYear = intent.getStringExtra("extra_modify_year");
        final String oldMonth = intent.getStringExtra("extra_modify_month");
        final String oldTitle = intent.getStringExtra("extra_modify_title");
        final String oldContent = intent.getStringExtra("extra_modify_content");
        final String oldLocation = intent.getStringExtra("extra_modify_location");
        final String oldDate = intent.getStringExtra("extra_modify_date");
        final int cmpId = intent.getIntExtra("extra_cmpId", 0);
        oldNote.setYear(oldYear);
        oldNote.setMonth(oldMonth);
        oldNote.setTitle(oldTitle);
        oldNote.setContent(oldContent);
        oldNote.setLoacation(oldLocation);
        oldNote.setDate(oldDate);
        oldNote.setCmpId(cmpId);

        if (oldTitle != null && !oldTitle.equals("")) {
            etTitle.setText(oldTitle);
            etContent.setText(oldContent);
            etLocation.setText(oldLocation);
        }
        mNoteDbInstance = NoteDbInstance.getInstance(this);
        btWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                content = etContent.getText().toString();
                location = etLocation.getText().toString();
                newNote.setTitle(title);
                newNote.setContent(content);
                newNote.setLoacation(location);
                newNote.setCmpId(oldNote.getCmpId());
                newNote.setHasModified(1);
                mNoteDbInstance.UpdateNote(oldNote, newNote);
                updateBmobNote(newNote);

                // 回到主活动
                Intent intent = new Intent(ModifyNoteActivity.this, NoteTitleActivity.class);
                intent.putExtra("extra_noteYear", oldNote.getYear());
                intent.putExtra("extra_noteMonth", oldNote.getMonth());
                startActivity(intent);
            }
        });

    }

    private void updateBmobNote(final Note note) {
        BmobQuery<BmobNote> query = new BmobQuery<>();
        query.addWhereEqualTo("cmpId", note.getCmpId());
        query.findObjects(ModifyNoteActivity.this, new FindListener<BmobNote>() {
            @Override
            public void onSuccess(List<BmobNote> list) {
                if (list.size() > 0) {
                    BmobNote bmobNote = new BmobNote();
                    bmobNote.setTitle(note.getTitle());
                    bmobNote.setContent(note.getContent());
                    bmobNote.setLoacation(note.getLoacation());
                    bmobNote.update(ModifyNoteActivity.this, list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            note.setHasModified(0);
                            mNoteDbInstance.UpdateNote(note, note);
                            LogUtils.i("update bmbo note success.");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogUtils.e("update bmob note failed:\n" + s);
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e("query bmob oldNote cmpId failed:\n" + s);
            }
        });
    }
}
