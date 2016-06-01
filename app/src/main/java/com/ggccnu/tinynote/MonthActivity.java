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

import java.util.List;


public class MonthActivity extends Activity {

    private NoteDb mNoteDb;
    private RecyclerView mRecyclerView;
    private TitleCustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;
    public List<String> mMonthList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        // 从intent中恢复笔记year，
        Intent intent = getIntent();
        final String year = intent.getStringExtra("extra_noteYear");
        mNoteDb = NoteDb.getInstance(this);
        mMonthList = mNoteDb.QueryMonths(year);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_month_list);
        mCustomAdaptor = new TitleCustomAdapter(mMonthList);
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
        mCustomAdaptor.setOnItemClickLitener(new TitleCustomAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("monthActivity", "Element " + position + " set.");
                String selectedMonth = mMonthList.get(position);
                // 启动日记查看编辑活动，同时将日记year,month传递过去
                Intent intent = new Intent(MonthActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", selectedMonth);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        TextViewVertical tvYear = (TextViewVertical) findViewById(R.id.title_year);
        tvYear.setText(year);
        Button btnWrite = (Button) findViewById(R.id.button_write);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
        //如果只有当年的笔记，直接进入月视图
//        if(mMonthList.size() == 1) {
//            //延时1s
//            SystemClock.sleep(100);
//            String selectedYear = mMonthList.get(0);
//            // 启动日记查看编辑活动，同时将日记year传递过去
//            Intent mIntent = new Intent(MonthActivity.this, MainActivity.class);
//            intent.putExtra("extra_noteMonth", mMonthList.get(0));
//            startActivity(mIntent);
//        }
    }
}
