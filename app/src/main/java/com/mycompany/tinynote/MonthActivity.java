package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mycompany.tinynote.db.NoteDb;

import java.util.List;


public class MonthActivity extends Activity {

    private TextViewVertical titleYear;
    private NoteDb noteDb;
    private String year, month;

    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<String> monthItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        // 从intent中恢复笔记year，
        Intent intent = getIntent();
        year = intent.getStringExtra("extra_noteYear");

        //new LongOperation().execute("");
        noteDb = NoteDb.getInstance(this);
        monthItemList = noteDb.QueryMonths(year);
        mRecyclerView = (RecyclerView)findViewById(R.id.month_list);
        mCustomAdaptor = new CustomAdapter(monthItemList);
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
                Log.d("monthActivity", "Element " + position + " set.");
                String selectedMonth = monthItemList.get(position);
                // 启动日记查看编辑活动，同时将日记title,month传递过去
                Intent intent = new Intent(MonthActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", selectedMonth);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        titleYear = (TextViewVertical) findViewById(R.id.title_year);
        titleYear.setText(year);
    }

}
