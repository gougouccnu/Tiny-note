package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private MyDatabaseHelper dbHelper;
    private String content;
    private String location;


    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;
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
                "title=? and month=? and year=?", new String[] {title, month, year}, null, null, null);
        if (cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex("content"));
            location = cursor.getString(cursor.getColumnIndex("location"));
        } else { //若表为空，则...
            content = "null";
            location = "null";
        }
        cursor.close();

        NotesItem itemTitle = new NotesItem(title);
        notesItemList.add(itemTitle);
        String[] mcontentString= content.split("\\n");
        int mcontentLength = mcontentString.length;
        for(int i = 0; i < mcontentLength; i++) {
            String mtemp = mcontentString[i];
            NotesItem item = new NotesItem(mtemp);
            notesItemList.add(item);
        }
        if (mcontentLength < 10) {
            for (int i = 0; i < (5-mcontentLength); i++) {
                NotesItem itemBlank = new NotesItem(" ");
                notesItemList.add(itemBlank);
            }
        }
        NotesItem itemLocation = new NotesItem(location);
        notesItemList.add(itemLocation);
        NotesItem itemDate = new NotesItem(year + month);
        notesItemList.add(itemDate);

        mRecyclerView = (RecyclerView)findViewById(R.id.note_edit_content);
        mCustomAdaptor = new CustomAdapter(notesItemList);
        mRecyclerView.setAdapter(mCustomAdaptor);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);
        mCustomAdaptor.setOnItemClickLitener(new CustomAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                // toggle button display status
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
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
