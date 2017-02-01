package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.adapter.TitleAdapter;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.widget.TextViewVertical;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.util.ArrayList;
import java.util.List;

public class NoteTitleActivity extends Activity {

    private NoteDbInstance mNoteDbInstance;
    private RecyclerView mRecyclerView;
    private TitleAdapter mTitleAdaptor;
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

        mNoteDbInstance = NoteDbInstance.getInstance(this);
        mTitleList = mNoteDbInstance.QueryTitles(year, month);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_note_item);
        mTitleAdaptor = new TitleAdapter(mTitleList);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mTitleAdaptor);

        // 显示最新日期的笔记
        mRecyclerView.scrollToPosition(mTitleList.size() - 1);
        mTitleAdaptor.setOnItemClickLitener(new TitleAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.d("NoteTitleActivity", "Element " + position + " set.");
                String selectedTitle = mTitleList.get(position);

                // 启动日记查看编辑活动，同时将日记title,month,year传递过去
                Intent intent = new Intent(NoteTitleActivity.this, EditNoteActivity.class);
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
                startActivity(new Intent(NoteTitleActivity.this, WriteNoteActivity.class));
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

        mNoteDbInstance = NoteDbInstance.getInstance(this);
        mTitleList = mNoteDbInstance.QueryTitles(year, month);
        mTitleAdaptor.update((ArrayList<String>) mTitleList);
        // 显示最新日期的笔记
        mRecyclerView.scrollToPosition(mTitleList.size() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, "main page");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }
}
