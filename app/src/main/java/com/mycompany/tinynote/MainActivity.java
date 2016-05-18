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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private TextViewVertical titleYear;
    private Button buttonWrite;
    private TextViewVertical titleMonth;

    private NoteDb noteDb;
    private String year,month,title;

    private RecyclerView mRecyclerView;
    private NoteTitleCustomAdapter mCustomAdaptor;
    //private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager;
    private int scrollPosition;

    public List<String> noteItemList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 从intent中恢复笔记year，month
        Intent intent = getIntent();
        year = intent.getStringExtra("extra_noteYear");
        month = intent.getStringExtra("extra_noteMonth");
        titleYear = (TextViewVertical) findViewById(R.id.title_year);
        titleMonth = (TextViewVertical) findViewById(R.id.title_month);
        titleYear.setText(year);
        titleMonth.setText(month);

        noteDb = NoteDb.getInstance(this);
        //new LongOperation().execute("");
        noteItemList = noteDb.QueryTitles(year, month);
        mRecyclerView = (RecyclerView)findViewById(R.id.note_item);
        mCustomAdaptor = new NoteTitleCustomAdapter(noteItemList);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mCustomAdaptor);

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        // 显示最新日期的笔记
        mRecyclerView.scrollToPosition(noteItemList.size() - 1);
        mCustomAdaptor.setOnItemClickLitener(new NoteTitleCustomAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("MainActivity", "Element " + position + " set.");
                String selectedTitle = noteItemList.get(position);
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
        buttonWrite = (Button) findViewById(R.id.button_write);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView.getChildCount();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.getChildCount();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart");
        // 从intent中恢复笔记year，month
        Intent intent = getIntent();
        year = intent.getStringExtra("extra_noteYear");
        month = intent.getStringExtra("extra_noteMonth");
        noteDb = NoteDb.getInstance(this);
        //new LongOperation().execute("");
        noteItemList = noteDb.QueryTitles(year, month);
//        mCustomAdaptor.setOnItemClickLitener(new NoteTitleCustomAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Log.d("MainActivity", "Element " + position + " set.");
//                String selectedTitle = noteItemList.get(position);
//                // 启动日记查看编辑活动，同时将日记title,month,year传递过去
//                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
//                intent.putExtra("extra_noteYear", year);
//                intent.putExtra("extra_noteMonth", month);
//                intent.putExtra("extra_noteTitle", selectedTitle);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                mCustomAdaptor.notifyDataSetChanged();
//            }
//        });
        mCustomAdaptor.update((ArrayList<String>) noteItemList);
//        mCustomAdaptor.notifyDataSetChanged();
    }
}
