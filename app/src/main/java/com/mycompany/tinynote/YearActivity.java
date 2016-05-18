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

public class YearActivity extends Activity {

    //private MyDatabaseHelper dbHelper;
    private NoteDb noteDb;
    //private String year;
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<String> yearItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        //new LongOperation().execute("");
        noteDb = NoteDb.getInstance(this);
        yearItemList = noteDb.QueryYears();

        mRecyclerView = (RecyclerView) findViewById(R.id.year_list);
        mCustomAdaptor = new CustomAdapter(yearItemList);
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
                String selectedYear = yearItemList.get(position);
                // 启动日记查看编辑活动，同时将日记year传递过去
                Intent intent = new Intent(YearActivity.this, MonthActivity.class);
                intent.putExtra("extra_noteYear", selectedYear);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        //如果只有当年的笔记，直接进入月视图
        if(yearItemList.size() == 1) {
            //延时1s

            String selectedYear = yearItemList.get(0);
            // 启动日记查看编辑活动，同时将日记year传递过去
            Intent intent = new Intent(YearActivity.this, MonthActivity.class);
            intent.putExtra("extra_noteYear", selectedYear);
            startActivity(intent);
        }

    }
    /*
    private void yearListInit() {
        new LongOperation().execute("");
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            dbHelper = new MyDatabaseHelper(YearActivity.this, "NoteStore.db", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //查询NOTE表中最近月份的笔记并显示出来，
            //db.rawQuery("select id,year,month from Note where id = 1", null);
            Cursor cursor = db.rawQuery("SELECT DISTINCT year FROM Note ORDER BY " +
                    "year ASC", null);
            //Cursor cursor = db.query("Note", new String[]{"id", "year", "month"},
            //        null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    //遍历cursor对象，取出数据
                    year = cursor.getString(cursor.getColumnIndex("year"));
                    //添加到note list中
                    NotesItem item = new NotesItem(year);
                    yearItemList.add(item);
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
    */
}
