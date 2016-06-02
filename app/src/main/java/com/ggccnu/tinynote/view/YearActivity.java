package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.adapter.TitleCustomAdapter;
import com.ggccnu.tinynote.db.NoteDb;
import com.ggccnu.tinynote.util.DateConvertor;

import java.util.Calendar;
import java.util.List;

public class YearActivity extends Activity {

    //private MyDatabaseHelper dbHelper;
    private NoteDb mNoteDb;
    //private String year;
    private RecyclerView mRecyclerView;
    private TitleCustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<String> mYearList;
    public static final int GOTO_MONTH_ACTIVITY = 1;

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
        mCustomAdaptor = new TitleCustomAdapter(mYearList);
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
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GOTO_MONTH_ACTIVITY:
                        // 启动日记查看编辑活动，同时将日记year传递过去
                        Intent intent = new Intent(YearActivity.this, MonthActivity.class);
                        intent.putExtra("extra_noteYear", mYearList.get(0));
                        startActivity(intent);
                    default:
                        break;
                }
            }
        };
        //如果只有当年的笔记，直接进入月视图
        if(mYearList.size() == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    }
                    catch (InterruptedException e) {

                    }
                    finally {

                    }
                    Message message = new Message();
                    message.what = GOTO_MONTH_ACTIVITY;
                    handler.sendMessage(message);
                }
            }).start();
        }

    }
}
