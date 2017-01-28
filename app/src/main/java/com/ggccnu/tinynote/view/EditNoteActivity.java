package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.adapter.CustomAdapter;
import com.ggccnu.tinynote.db.NoteDb;
import com.ggccnu.tinynote.model.Note;
import com.ggccnu.tinynote.widget.MyDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishaowei on 15/10/5.
 */
public class EditNoteActivity extends Activity {

    private Button buttonModify;
    private Button buttonSave;
    private Button buttonDelete;
    private NoteDb mNoteDb;
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;
    private Note mNote;
    private List<String> mNoteContentList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        // 从intent中恢复笔记title,month，
        Intent intent = getIntent();
        final String year = intent.getStringExtra("extra_noteYear");
        final String title = intent.getStringExtra("extra_noteTitle");
        final String month = intent.getStringExtra("extra_noteMonth");

        mNoteDb = NoteDb.getInstance(this);
        mNote = mNoteDb.QueryNoteAll(year, month, title);
        mNoteContentList.add(mNote.getTitle());
        String[] mcontentString= mNote.getContent().split("\\n");
        int mcontentLength = mcontentString.length;
        for(int i = 0; i < mcontentLength; i++) {
            String item = mcontentString[i];
            // 笔记标题后空格
            if (i == 0) {
                mNoteContentList.add(" ");
                mNoteContentList.add(item);
            } else {
                mNoteContentList.add(item);
            }
        }
        // 笔记显示不满一屏，在内容后加空格，让笔记结尾与屏幕左边对其
        if (mcontentLength < 4) {
            for (int i = 0; i < (8-mcontentLength); i++) {
                mNoteContentList.add(" ");
            }
        } else if (mcontentLength <7) {
            for (int i = 0; i < (7-mcontentLength); i++) {
                mNoteContentList.add(" ");
            }
        } else {
            mNoteContentList.add(" ");
        }
        mNoteContentList.add(mNote.getLoacation());
        mNoteContentList.add(mNote.getDate());

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_note_edit_content);
        mCustomAdaptor = new CustomAdapter(mNoteContentList);
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
                // toggle button display status
                if (buttonDelete.getVisibility() == View.INVISIBLE) {
                    buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    buttonDelete.setVisibility(View.INVISIBLE);
                }
                if (buttonSave.getVisibility() == View.INVISIBLE) {
                    buttonSave.setVisibility(View.VISIBLE);
                } else {
                    buttonSave.setVisibility(View.INVISIBLE);
                }
                if (buttonModify.getVisibility() == View.INVISIBLE) {
                    buttonModify.setVisibility(View.VISIBLE);
                } else {
                    buttonModify.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        // 修改按钮
        buttonModify = (Button) findViewById(R.id.edit);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditNoteActivity.this, ModifyNoteActivity.class);
                intent.putExtra("extra_modify_year", mNote.getYear());
                intent.putExtra("extra_modify_month", mNote.getMonth());
                intent.putExtra("extra_modify_title", mNote.getTitle());
                intent.putExtra("extra_modify_content", mNote.getContent());
                intent.putExtra("extra_modify_location", mNote.getLoacation());
                intent.putExtra("extra_modify_date", mNote.getDate());
                startActivity(intent);
            }
        });
        // 保持日记按钮
        buttonSave = (Button) findViewById(R.id.save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // v为保存按钮，这里需要整个界面
                // 下面这个无法保存完成界面，原因未知
                // saveScreenShot(findViewById(android.R.id.content));
                saveScreenShot(getWindow().getDecorView().getRootView());
            }
        });
        // 删除日记按钮
        buttonDelete = (Button) findViewById(R.id.delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyDialogFragment myDialogFragment = new MyDialogFragment() {
                    @Override
                    public void dialogPositiveButtonClicked() {
                        LogUtils.d("EditNoteActivity", "positive button clicked");
                        mNoteDb.DeleteNote(mNote);
                        // 启动日记查看编辑活动，同时将日记title,month传递过去
                        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                        intent.putExtra("extra_noteYear", mNote.getYear());
                        intent.putExtra("extra_noteMonth", mNote.getMonth());
                        startActivity(intent);
                    }

                    @Override
                    public void dialogNegativeButtonClicked() {
                        dismiss();
                    }
                };
                myDialogFragment.show(getFragmentManager(), "对话框");
            }
        });
    }

    /**
     * 读取bitmap来截屏,必须保存到SD卡，因为其他程序无法访问tinyNote内的数据
     * @param view
     */
    private void saveScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + "tinynoteshare.png";
            File imagePath = new File(filePath);
            try {
                FileOutputStream fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            shareCapture(filePath);
        }
    }

    /**
     * 使用Intent共享截图
     * @param filePath
     */
    private void shareCapture(String filePath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        //Uri uri = Uri.fromFile(getFileStreamPath(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
        //Uri uri = Uri.fromFile(getFileStreamPath("lswscreenshot.png"));
        Uri uri = Uri.parse("file://" + filePath);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(shareIntent);
    }
}
