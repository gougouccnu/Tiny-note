package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishaowei on 15/10/5.
 */
public class EditNoteActivity extends Activity {

    private Button buttonModify;
    private Button buttonSave;
    private Button buttonDelete;

    private TextViewVertical noteEditLocation;
    private TextViewVertical noteEditContent;
    private TextViewVertical noteEditTitle;

    private MyDatabaseHelper dbHelper;
    private String content;
    private String location;


    private HorizontalListViewAdapter hlva;
    private HorizontalListView hlv;

    public List<NotesItem> notesItemList = new ArrayList<NotesItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        // 从intent中恢复笔记title,month，
        Intent intent = getIntent();
        final String year = intent.getStringExtra("extra_noteYear");
        final String title = intent.getStringExtra("extra_noteTitle");
        final String month = intent.getStringExtra("extra_noteMonth");

        dbHelper = new MyDatabaseHelper(this, "NoteStore.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 查询数据库得到日记其他信息
        Cursor cursor = db.query("Note", new String[] {"content","location"},
                "title=? and month=?", new String[] {title, month}, null, null, null);
        if (cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex("content"));
            location = cursor.getString(cursor.getColumnIndex("location"));
        } else { //若表为空，则...
            content = "null";
            location = "null";
        }
        cursor.close();

        Typeface customFont = Typeface.createFromAsset(this.getAssets(), "fonts/KangXi.ttf");
        //titleYear = (TextViewVertical) findViewById(R.id.title_year);
        //titleYear.setTypeface(customFont);

        // 显示该日记
        noteEditTitle = (TextViewVertical) findViewById(R.id.note_edit_title);
        noteEditTitle.setTypeface(customFont);
        //noteEditContent = (TextViewVertical) findViewById(R.id.note_edit_content);
        noteEditLocation = (TextViewVertical) findViewById(R.id.note_edit_location);
        noteEditLocation.setTypeface(customFont);
        noteEditTitle.setText(title);
        //noteEditContent.setText(content);
        noteEditLocation.setText(location);

        String[] mcontentString= content.split("\\n");
        for(int i = 0; i < mcontentString.length; i++) {
            String mtemp = mcontentString[mcontentString.length - i - 1];
            NotesItem item = new NotesItem(mtemp);
            notesItemList.add(item);
        }
        //显示日记内容
        hlv = (HorizontalListView)findViewById(R.id.note_edit_content);
        hlva = new HorizontalListViewAdapter(this, notesItemList);
        //hlva.notifyDataSetChanged();
        hlv.setAdapter(hlva);


        // 修改按钮
        buttonModify = (Button) findViewById(R.id.edit);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditNoteActivity.this, ModifyNoteActivity.class);
                intent.putExtra("extra_modify_title", title);
                intent.putExtra("extra_modify_content", content);
                intent.putExtra("extra_modify_location", location);
                startActivity(intent);
            }
        });
        // 保持日记按钮
        buttonSave = (Button) findViewById(R.id.save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 删除日记按钮
        buttonDelete = (Button) findViewById(R.id.delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deletRow = db.delete("Note", "title = ? and content = ?",
                        new String[] {title,content});
//                if (deletRow == 1) {
                    Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                    startActivity(intent);
//                }
            }
        });


    }
}
