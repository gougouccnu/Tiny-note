package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mycompany.tinynote.db.NoteDb;
import com.mycompany.tinynote.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishaowei on 15/10/5.
 */
public class EditNoteActivity extends Activity {

    private Button buttonModify;
    private Button buttonSave;
    private Button buttonDelete;
    private NoteDb noteDb;
    private String content;
    private String location;


    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;
    private Note note;
    private List<String> notesItemList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        // 从intent中恢复笔记title,month，
        Intent intent = getIntent();
        final String year = intent.getStringExtra("extra_noteYear");
        final String title = intent.getStringExtra("extra_noteTitle");
        final String month = intent.getStringExtra("extra_noteMonth");

        noteDb = NoteDb.getInstance(this);
        note = noteDb.QueryNoteAll(year, month, title);
        notesItemList.add(note.getTitle());
        String[] mcontentString= note.getContent().split("\\n");
        int mcontentLength = mcontentString.length;
        for(int i = 0; i < mcontentLength; i++) {
            String item = mcontentString[i];
            notesItemList.add(item);
        }
        if (mcontentLength < 12) {
            for (int i = 0; i < (12-mcontentLength); i++) {
                String item = " ";
                notesItemList.add(item);
            }
        }
        notesItemList.add(note.getLoacation());
        notesItemList.add(note.getDate());

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
                intent.putExtra("extra_modify_year", note.getYear());
                intent.putExtra("extra_modify_month", note.getMonth());
                intent.putExtra("extra_modify_title", note.getTitle());
                intent.putExtra("extra_modify_content", note.getContent());
                intent.putExtra("extra_modify_location", note.getLoacation());
                intent.putExtra("extra_modify_date", note.getDate());
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
                //确认是否要删除 TODO
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.show(getFragmentManager(), "dialog");
                //noteDb.DeleteNote(note);
                // 启动日记查看编辑活动，同时将日记title,month传递过去
                Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", note.getYear());
                intent.putExtra("extra_noteMonth", note.getMonth());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("EditNoteActivity", "onRestart");
    }
}
