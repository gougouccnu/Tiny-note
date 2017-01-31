package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.utils.LogUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.Utils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.adapter.TitleAdapter;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.update.UpdateChecker;
import com.ggccnu.tinynote.util.DateConvertor;

import java.util.Calendar;
import java.util.List;

public class YearActivity extends Activity {

    private NoteDbInstance mNoteDbInstance;

    private RecyclerView mRecyclerView;
    private TitleAdapter mTitleAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> mYearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        // 检查是否要更新APP。要初始化，否则得不到context
        Utils.init(this);
        if (NetworkUtils.getWifiEnabled()) {
            UpdateChecker.checkForDialog(this);
        }

        mNoteDbInstance = NoteDbInstance.getInstance(this);
        mYearList = mNoteDbInstance.QueryYears();
        // 笔记为空，显示当前年
        if (mYearList.isEmpty()) {
            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            mYearList.add(DateConvertor.formatYear(currentYear));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_year);
        mTitleAdaptor = new TitleAdapter(mYearList);
        mRecyclerView.setAdapter(mTitleAdaptor);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);

        mTitleAdaptor.setOnItemClickLitener(new TitleAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.d("YearActivity", "Element " + position + " set.");
                // 启动日记查看编辑活动，同时将日记year传递过去
                Intent intent = new Intent(YearActivity.this, MonthActivity.class);
                intent.putExtra("extra_noteYear", mYearList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    /*  还是让用户点击进入月视图吧
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
    */

    }
}
