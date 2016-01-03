package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MonthActivity extends Activity {

    private TextViewVertical titleYear;
    private MyDatabaseHelper dbHelper;
    private String year, month;

    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<NotesItem> monthItemList = new ArrayList<NotesItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        // 从intent中恢复笔记year，
        Intent intent = getIntent();
        year = intent.getStringExtra("extra_noteYear");

        new LongOperation().execute("");

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
                NotesItem item = monthItemList.get(position);
                // 启动日记查看编辑活动，同时将日记title,month传递过去
                Intent intent = new Intent(MonthActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", item.getTitle());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        titleYear = (TextViewVertical) findViewById(R.id.title_year);
        titleYear.setText(year);
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            dbHelper = new MyDatabaseHelper(MonthActivity.this, "NoteStore.db", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //查询NOTE表中最近月份的笔记并显示出来，
            //db.rawQuery("select id,year,month from Note where id = 1", null);
            Cursor cursor = db.rawQuery("SELECT DISTINCT month FROM Note WHERE year = ? ORDER BY " +
                    "month ASC", new String[] { year });
            if (cursor.moveToFirst()) {
                do {
                        //遍历cursor对象，取出数据
                        month = cursor.getString(cursor.getColumnIndex("month"));
                        //添加到note list中
                        NotesItem item = new NotesItem(month);
                        monthItemList.add(item);
                        //noteItemList.
                } while (cursor.moveToNext());
            }
            cursor.close();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
