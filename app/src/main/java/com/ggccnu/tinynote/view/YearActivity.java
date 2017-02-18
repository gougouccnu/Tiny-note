package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.utils.LogUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;
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

    private static int backPressedCnt = 0;

    private DrawerLayout mDrawerLayout;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        // 检查是否要更新APP。要初始化，否则得不到context
        Utils.init(this);
        if (NetworkUtils.getWifiEnabled()) {
            UpdateChecker.checkForDialog(this);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.simple_navigation_drawer);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YearActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressedCnt++;
        if (backPressedCnt < 2) {
            ToastUtils.showLongToast("再按一次返回键退出APP");
        } else {
            this.finish();
            //System.exit(0); system.exit 看起来不是好的退出方式
        }
    }
//    这种方式也可以退出，注意要调用System.exit(0)
//    private boolean mIsExit;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mIsExit) {
//                this.finish();
//                System.exit(0); system.exit 看起来不是好的退出方式
//            } else {
//                ToastUtils.showLongToast("再按一次退出");
//                mIsExit = true;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mIsExit = false;
//                    }
//                }, 2000);
//            }
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}
