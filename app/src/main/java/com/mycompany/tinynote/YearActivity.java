package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mycompany.tinynote.db.NoteDb;
import com.mycompany.tinynote.util.DateConvertor;

import java.util.Calendar;
import java.util.List;

public class YearActivity extends Activity {

    //private MyDatabaseHelper dbHelper;
    private NoteDb mNoteDb;
    //private String year;
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<String> mYearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        mNoteDb = NoteDb.getInstance(this);
        mYearList = mNoteDb.QueryYears();
        // 笔记为空，显示当前年
        if (mYearList.isEmpty()) {
            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            mYearList.add(DateConvertor.formatYear(currentYear));
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_year);
        mCustomAdaptor = new CustomAdapter(mYearList);
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
                Log.d("YearActivity", "Element " + position + " set.");
                // 启动日记查看编辑活动，同时将日记year传递过去
                Intent intent = new Intent(YearActivity.this, MonthActivity.class);
                intent.putExtra("extra_noteYear", mYearList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        //如果只有当年的笔记，直接进入月视图
        if(mYearList.size() == 1) {
            //延时1s
            SystemClock.sleep(1000);
            // 启动日记查看编辑活动，同时将日记year传递过去
            Intent intent = new Intent(YearActivity.this, MonthActivity.class);
            intent.putExtra("extra_noteYear", mYearList.get(0));
            startActivity(intent);
        }

    }
}
