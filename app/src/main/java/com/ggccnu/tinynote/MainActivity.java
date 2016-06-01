package com.ggccnu.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.ggccnu.tinynote.db.NoteDb;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private NoteDb mNoteDb;
    private RecyclerView mRecyclerView;
    private TitleCustomAdapter mCustomAdaptor;
    private LinearLayoutManager mLayoutManager;
    private List<String> mTitleList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 从intent中恢复笔记year，month
        Intent intent = getIntent();
        final String year = intent.getStringExtra("extra_noteYear");
        final String month = intent.getStringExtra("extra_noteMonth");
        TextViewVertical tvYear = (TextViewVertical) findViewById(R.id.title_year);
        TextViewVertical tvMonth = (TextViewVertical) findViewById(R.id.title_month);
        tvYear.setText(year);
        tvMonth.setText(month);

        mNoteDb = NoteDb.getInstance(this);
        mTitleList = mNoteDb.QueryTitles(year, month);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_note_item);
        mCustomAdaptor = new TitleCustomAdapter(mTitleList);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mCustomAdaptor);
        
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        // 显示最新日期的笔记
        mRecyclerView.scrollToPosition(mTitleList.size() - 1);
        mCustomAdaptor.setOnItemClickLitener(new TitleCustomAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("MainActivity", "Element " + position + " set.");
                String selectedTitle = mTitleList.get(position);
                // 启动日记查看编辑活动，同时将日记title,month,year传递过去
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", month);
                intent.putExtra("extra_noteTitle", selectedTitle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        //write display
        Button btnWrite = (Button) findViewById(R.id.button_write);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        // 从intent中恢复笔记year，month
        Intent intent = getIntent();
        String year = intent.getStringExtra("extra_noteYear");
        String month = intent.getStringExtra("extra_noteMonth");
        mNoteDb = NoteDb.getInstance(this);
        mTitleList = mNoteDb.QueryTitles(year, month);
        mCustomAdaptor.update((ArrayList<String>) mTitleList);
        // 显示最新日期的笔记
        mRecyclerView.scrollToPosition(mTitleList.size() - 1);
    }
}
